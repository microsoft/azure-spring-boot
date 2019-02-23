/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.keyvault.spring;

import com.microsoft.azure.AzureEnvironment;
import com.microsoft.azure.AzureResponseBuilder;
import com.microsoft.azure.credentials.AppServiceMSICredentials;
import com.microsoft.azure.keyvault.KeyVaultClient;
import com.microsoft.azure.serializer.AzureJacksonAdapter;
import com.microsoft.azure.spring.support.UserAgent;
import com.microsoft.azure.telemetry.TelemetryProxy;
import com.microsoft.azure.utils.PropertyLoader;
import com.microsoft.rest.RestClient;
import com.microsoft.rest.credentials.ServiceClientCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.microsoft.azure.telemetry.TelemetryData.SERVICE_NAME;
import static com.microsoft.azure.telemetry.TelemetryData.getClassPackageSimpleName;

class KeyVaultEnvironmentPostProcessorHelper {
    private static final Logger LOG = LoggerFactory.getLogger(KeyVaultEnvironmentPostProcessorHelper.class);

    private final ConfigurableEnvironment environment;
    private final TelemetryProxy telemetryProxy;

    public KeyVaultEnvironmentPostProcessorHelper(final ConfigurableEnvironment environment) {
        this.environment = environment;
        // As auto-configuration not available when post processor, load it from file.
        this.telemetryProxy = new TelemetryProxy(PropertyLoader.getTelemetryInstrumentationKey());
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
            final KeyVaultOperation kvOperation = new KeyVaultOperation(kvClient, vaultUri, refreshInterval);

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
        }

        final long timeAcquiringTimeoutInSeconds = this.environment.getProperty(
                Constants.AZURE_TOKEN_ACQUIRE_TIMEOUT_IN_SECONDS, Long.class, Constants.TOKEN_ACQUIRE_TIMEOUT_SECS);
        if (this.environment.containsProperty(Constants.AZURE_KEYVAULT_CLIENT_ID)
                && this.environment.containsProperty(Constants.AZURE_KEYVAULT_CLIENT_KEY)) {
            LOG.debug("Will use custom credentials");
            final String clientId = getProperty(this.environment, Constants.AZURE_KEYVAULT_CLIENT_ID);
            final String clientKey = getProperty(this.environment, Constants.AZURE_KEYVAULT_CLIENT_KEY);
            return new AzureKeyVaultCredential(clientId, clientKey, timeAcquiringTimeoutInSeconds);
        }

        if (this.environment.containsProperty(Constants.AZURE_KEYVAULT_CLIENT_ID) &&
                this.environment.containsProperty(Constants.AZURE_KEYVAULT_CERTIFICATE_PATH)) {
            final String clientId = getProperty(this.environment, Constants.AZURE_KEYVAULT_CLIENT_ID);
            // Password can be empty
            final String certPwd = this.environment.getProperty(Constants.AZURE_KEYVAULT_CERTIFICATE_PASSWORD);
            final String certPath = getProperty(this.environment, Constants.AZURE_KEYVAULT_CERTIFICATE_PATH);

            LOG.info("Read certificate from {}...", certPath);
            final Resource certResource = new DefaultResourceLoader().getResource(certPath);

            return new KeyVaultCertificateCredential(clientId, certResource, certPwd, timeAcquiringTimeoutInSeconds);
        }

        if (this.environment.containsProperty(Constants.AZURE_KEYVAULT_CLIENT_ID)) {
            LOG.debug("Will use MSI credentials for VMs with specified clientId");
            final String clientId = getProperty(this.environment, Constants.AZURE_KEYVAULT_CLIENT_ID);
            return new AzureKeyVaultMSICredential(AzureEnvironment.AZURE, clientId);
        }

        LOG.debug("Will use MSI credentials for VM");
        return new AzureKeyVaultMSICredential(AzureEnvironment.AZURE);
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
        if (allowTelemetry(environment)) {
            final Map<String, String> events = new HashMap<>();

            events.put(SERVICE_NAME, getClassPackageSimpleName(KeyVaultEnvironmentPostProcessorHelper.class));

            telemetryProxy.trackEvent(ClassUtils.getUserClass(getClass()).getSimpleName(), events);
        }
    }
}
