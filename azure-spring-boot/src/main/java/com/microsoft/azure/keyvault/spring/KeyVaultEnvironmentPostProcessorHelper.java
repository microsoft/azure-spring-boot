/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.keyvault.spring;

import com.azure.core.credential.TokenCredential;
import com.azure.core.http.policy.HttpLogOptions;
import com.azure.identity.ClientCertificateCredentialBuilder;
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.identity.ManagedIdentityCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.microsoft.azure.telemetry.TelemetrySender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.microsoft.azure.keyvault.spring.Constants.SPRINGBOOT_KEY_VAULT_APPLICATION_ID;
import static com.microsoft.azure.telemetry.TelemetryData.SERVICE_NAME;
import static com.microsoft.azure.telemetry.TelemetryData.getClassPackageSimpleName;

@Slf4j
class KeyVaultEnvironmentPostProcessorHelper {

    private final ConfigurableEnvironment environment;

    public KeyVaultEnvironmentPostProcessorHelper(final ConfigurableEnvironment environment) {
        this.environment = environment;
        // As @PostConstructor not available when post processor, call it explicitly.
        sendTelemetry();
    }

    public void addKeyVaultPropertySource() {
        final String vaultUri = getProperty(this.environment, Constants.AZURE_KEYVAULT_VAULT_URI);
        final Long refreshInterval = Optional.ofNullable(
                this.environment.getProperty(Constants.AZURE_KEYVAULT_REFRESH_INTERVAL))
                .map(Long::valueOf).orElse(Constants.DEFAULT_REFRESH_INTERVAL_MS);
        final Binder binder = Binder.get(this.environment);
        final List<String> secretKeys = binder.bind(Constants.AZURE_KEYVAULT_SECRET_KEYS, Bindable.listOf(String.class))
                .orElse(Collections.emptyList());

        final TokenCredential tokenCredential = getCredentials();
        final SecretClient secretClient = new SecretClientBuilder()
                .vaultUrl(vaultUri)
                .credential(tokenCredential)
                .httpLogOptions(new HttpLogOptions().setApplicationId(SPRINGBOOT_KEY_VAULT_APPLICATION_ID))
                .buildClient();
        try {
            final MutablePropertySources sources = this.environment.getPropertySources();
            final boolean caseSensitive = Boolean.getBoolean(
                    this.environment.getProperty(Constants.AZURE_KEYVAULT_CASE_SENSITIVE_KEYS, "false"));
            final KeyVaultOperation kvOperation = new KeyVaultOperation(secretClient,
                    vaultUri,
                    refreshInterval,
                    secretKeys,
                    caseSensitive);

            if (sources.contains(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME)) {
                sources.addAfter(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME,
                        new KeyVaultPropertySource(kvOperation));
            } else {
                sources.addFirst(new KeyVaultPropertySource(kvOperation));
            }

        } catch (final Exception ex) {
            throw new IllegalStateException("Failed to configure KeyVault property source", ex);
        }
    }

    public TokenCredential getCredentials() {
        //use service principle to authenticate
        if (this.environment.containsProperty(Constants.AZURE_KEYVAULT_CLIENT_ID)
                && this.environment.containsProperty(Constants.AZURE_KEYVAULT_CLIENT_KEY)
                && this.environment.containsProperty(Constants.AZURE_KEYVAULT_TENANT_ID)) {
            log.debug("Will use custom credentials");
            final String clientId = getProperty(this.environment, Constants.AZURE_KEYVAULT_CLIENT_ID);
            final String clientKey = getProperty(this.environment, Constants.AZURE_KEYVAULT_CLIENT_KEY);
            final String tenantId = getProperty(this.environment, Constants.AZURE_KEYVAULT_TENANT_ID);
            final ClientSecretCredential clientSecretCredential = new ClientSecretCredentialBuilder()
                    .clientId(clientId)
                    .clientSecret(clientKey)
                    .tenantId(tenantId)
                    .build();
            return clientSecretCredential;
        }
        //use certificate to authenticate
        if (this.environment.containsProperty(Constants.AZURE_KEYVAULT_CLIENT_ID)
                && this.environment.containsProperty(Constants.AZURE_KEYVAULT_CERTIFICATE_PATH)
                && this.environment.containsProperty(Constants.AZURE_KEYVAULT_TENANT_ID)) {
            // Password can be empty
            final String certPwd = this.environment.getProperty(Constants.AZURE_KEYVAULT_CERTIFICATE_PASSWORD);
            final String certPath = getProperty(this.environment, Constants.AZURE_KEYVAULT_CERTIFICATE_PATH);

            if (StringUtils.isEmpty(certPwd)) {
                return new ClientCertificateCredentialBuilder()
                        .tenantId(getProperty(this.environment, Constants.AZURE_KEYVAULT_TENANT_ID))
                        .clientId(getProperty(this.environment, Constants.AZURE_KEYVAULT_CLIENT_ID))
                        .pemCertificate(certPath)
                        .build();
            } else {
                return new ClientCertificateCredentialBuilder()
                        .tenantId(getProperty(this.environment, Constants.AZURE_KEYVAULT_TENANT_ID))
                        .clientId(getProperty(this.environment, Constants.AZURE_KEYVAULT_CLIENT_ID))
                        .pfxCertificate(certPath, certPwd)
                        .build();
            }
        }
        //use MSI to authenticate
        if (this.environment.containsProperty(Constants.AZURE_KEYVAULT_CLIENT_ID)) {
            log.debug("Will use MSI credentials with specified clientId");
            final String clientId = getProperty(this.environment, Constants.AZURE_KEYVAULT_CLIENT_ID);
            return new ManagedIdentityCredentialBuilder().clientId(clientId).build();
        }
        log.debug("Will use MSI credentials");
        return new ManagedIdentityCredentialBuilder().build();
    }

    private String getProperty(final ConfigurableEnvironment env, final String propertyName) {
        Assert.notNull(env, "env must not be null!");
        Assert.notNull(propertyName, "propertyName must not be null!");
        final String property = env.getProperty(propertyName);
        if (property == null || property.isEmpty()) {
            throw new IllegalArgumentException("property " + propertyName + " must not be null");
        }
        return property;
    }

    private boolean allowTelemetry(final ConfigurableEnvironment env) {
        Assert.notNull(env, "env must not be null!");
        return env.getProperty(Constants.AZURE_KEYVAULT_ALLOW_TELEMETRY, Boolean.class, true);
    }

    private void sendTelemetry() {
        if (allowTelemetry(environment)) {
            final Map<String, String> events = new HashMap<>();
            final TelemetrySender sender = new TelemetrySender();

            events.put(SERVICE_NAME, getClassPackageSimpleName(KeyVaultEnvironmentPostProcessorHelper.class));

            sender.send(ClassUtils.getUserClass(getClass()).getSimpleName(), events);
        }
    }
}
