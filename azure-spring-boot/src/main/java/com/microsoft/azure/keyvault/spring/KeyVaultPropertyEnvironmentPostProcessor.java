/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.keyvault.spring;

import com.microsoft.azure.keyvault.KeyVaultClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.util.Assert;

public class KeyVaultPropertyEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {

        if (environment.getProperty(Constants.AZURE_CLIENTID) == null) {
            // User doesn't want to enable Key Vault property initializer.
            return;
        }

        final String clientId = getProperty(environment, Constants.AZURE_CLIENTID);
        final String clientKey = getProperty(environment, Constants.AZURE_CLIENTKEY);
        final String vaultUri = getProperty(environment, Constants.AZURE_KEYVAULT_VAULT_URI);

        boolean enabled = true;
        if (environment.getProperty(Constants.AZURE_KEYVAULT_ENABLED) != null) {
            enabled = Boolean.parseBoolean(environment.getProperty(Constants.AZURE_KEYVAULT_ENABLED));
        }

        long timeAcquiringTimeoutInSeconds = 60;
        if (environment.getProperty(Constants.AZURE_TOKEN_ACQUIRE_TIMEOUT_IN_SECONDS) != null) {
            timeAcquiringTimeoutInSeconds = Long.parseLong(
                    environment.getProperty(Constants.AZURE_TOKEN_ACQUIRE_TIMEOUT_IN_SECONDS));
        }

        if (enabled) {
            final KeyVaultClient kvClient = new KeyVaultClient(
                    new AzureKeyVaultCredential(clientId, clientKey, timeAcquiringTimeoutInSeconds));

            try {
                final MutablePropertySources sources = environment.getPropertySources();
                final KeyVaultOperation kvOperation = new KeyVaultOperation(kvClient, vaultUri);

                if (sources.contains(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME)) {
                    sources.addAfter(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME,
                            new KeyVaultPropertySource(kvOperation));
                } else {
                    sources.addFirst(new KeyVaultPropertySource(kvOperation));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getProperty(ConfigurableEnvironment env, String propertyName) {
        Assert.notNull(env, "env must not be null!");
        Assert.notNull(propertyName, "propertyName must not be null!");

        final String property = env.getProperty(propertyName);

        if (property == null || property.isEmpty()) {
            throw new IllegalArgumentException("property " + propertyName + " must not be null!");
        }
        return property;
    }
}
