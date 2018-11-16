/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.keyvault.spring;

import com.microsoft.azure.AzureEnvironment;
import com.microsoft.azure.AzureResponseBuilder;
import com.microsoft.azure.credentials.AppServiceMSICredentials;
import com.microsoft.azure.credentials.MSICredentials;
import com.microsoft.azure.keyvault.KeyVaultClient;
import com.microsoft.azure.serializer.AzureJacksonAdapter;
import com.microsoft.azure.spring.support.UserAgent;
import com.microsoft.azure.telemetry.TelemetryData;
import com.microsoft.azure.telemetry.TelemetryProxy;
import com.microsoft.rest.RestClient;
import com.microsoft.rest.credentials.ServiceClientCredentials;
import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.util.HashMap;
import java.util.Optional;

class KeyVaultEnvironmentPostProcessorHelper {
    private static final Logger LOG = LoggerFactory.getLogger(KeyVaultEnvironmentPostProcessorHelper.class);

    private final ConfigurableEnvironment environment;
    private final TelemetryProxy telemetryProxy;

    public KeyVaultEnvironmentPostProcessorHelper(final ConfigurableEnvironment environment) {
        this.environment = environment;
        this.telemetryProxy = new TelemetryProxy(this.allowTelemetry(environment));
    }

    public void addKeyVaultPropertySource() {
        final String vaultUri = getProperty(this.environment, Constants.AZURE_KEYVAULT_VAULT_URI);
        final Long refreshInterval = Optional.ofNullable(
                this.environment.getProperty(Constants.AZURE_KEYVAULT_REFRESH_INTERVAL))
                .map(Long::valueOf).orElse(Constants.DEFAULT_REFRESH_INTERVAL_MS);
        final ServiceClientCredentials credentials = getCredentials();

        final RestClient restClient = new RestClient.Builder().withBaseUrl(vaultUri)
                .withCredentials(credentials)
                .withSerializerAdapter(new AzureJacksonAdapter())
                .withResponseBuilderFactory(new AzureResponseBuilder.Factory())
                .withUserAgent(UserAgent.getUserAgent(Constants.AZURE_KEYVAULT_USER_AGENT,
                        allowTelemetry(this.environment)))
                .build();

        final KeyVaultClient kvClient = new KeyVaultClient(restClient);

        this.trackCustomEvent();

        try {
            final MutablePropertySources sources = this.environment.getPropertySources();
            final VaultPolicy policy = Optional
                    .ofNullable(this.environment.getProperty(Constants.AZURE_KEYVAULT_POLICY))
                    .map(value -> EnumUtils.getEnum(VaultPolicy.class, value))
                    .orElse(VaultPolicy.LIST);
            final KeyVaultOperation kvOperation = new KeyVaultOperation(kvClient, vaultUri, refreshInterval, policy);

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

    ServiceClientCredentials getCredentials() {
        if (this.environment.containsProperty("MSI_ENDPOINT")
                && this.environment.containsProperty("MSI_SECRET")) {
            LOG.debug("Will use MSI credentials for app services");
            final String msiEndpoint = getProperty(this.environment, "MSI_ENDPOINT");
            final String msiSecret = getProperty(this.environment, "MSI_SECRET");
            return new AppServiceMSICredentials(AzureEnvironment.AZURE, msiEndpoint, msiSecret);
        } else if (this.environment.containsProperty(Constants.AZURE_CLIENTID)
                && this.environment.containsProperty(Constants.AZURE_CLIENTKEY)) {
            LOG.debug("Will use custom credentials");
            final long timeAcquiringTimeoutInSeconds = this.environment.getProperty(
                    Constants.AZURE_TOKEN_ACQUIRE_TIMEOUT_IN_SECONDS, Long.class, Constants.TOKEN_ACQUIRE_TIMEOUT_SECS);
            final String clientId = getProperty(this.environment, Constants.AZURE_CLIENTID);
            final String clientKey = getProperty(this.environment, Constants.AZURE_CLIENTKEY);
            return new AzureKeyVaultCredential(clientId, clientKey, timeAcquiringTimeoutInSeconds);
        } else if (this.environment.containsProperty(Constants.AZURE_CLIENTID)) {
            LOG.debug("Will use MSI credentials for VMs with specified clientId");
            final String clientId = getProperty(this.environment, Constants.AZURE_CLIENTID);
            return new MSICredentials(AzureEnvironment.AZURE).withClientId(clientId);
        } else {
            LOG.debug("Will use MSI credentials for VM");
            return new MSICredentials(AzureEnvironment.AZURE);
        }
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

    private void trackCustomEvent() {
        final HashMap<String, String> customTelemetryProperties = new HashMap<>();
        customTelemetryProperties.put(TelemetryData.SERVICE_NAME, "keyvault");

        final String className = ClassUtils.getUserClass(this.getClass()).getSimpleName();
        this.telemetryProxy.trackEvent(className, customTelemetryProperties);
    }
}
