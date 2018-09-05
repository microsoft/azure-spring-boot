/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.keyvault.spring;

import com.microsoft.azure.keyvault.spring.secrets.KeyVaultPropertySource;
import com.microsoft.azure.keyvault.spring.secrets.KeyVaultSecretTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.ClassUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.microsoft.azure.keyvault.spring.Constants.*;

/**
 * Add {@link KeyVaultPropertySource} as last {@link org.springframework.context.annotation.PropertySource}
 * if this feature is enabled and related properties are provided
 * and {@link com.microsoft.azure.keyvault.KeyVaultClient} is on the classpath
 *
 * @author Warren Zhu
 */
public class KeyVaultEnvironmentPostProcessor implements EnvironmentPostProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(KeyVaultEnvironmentPostProcessor.class);

    private static final Set<String> AZURE_KEY_VAULT_PROPERTIES = Collections.unmodifiableSet(new HashSet<>(
            Arrays.asList(AZURE_CLIENT_ID, AZURE_KEYVAULT_CLIENT_KEY, AZURE_KEYVAULT_NAME)));

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (shouldAddKeyVaultPropertySource(environment)) {
            LOGGER.info("Azure Key Vault enabled.");
            final KeyVaultSecretTemplate operation = new KeyVaultSecretTemplate(
                    environment.getProperty(AZURE_CLIENT_ID), environment.getProperty(AZURE_KEYVAULT_CLIENT_KEY));

            operation.setAllowTelemetry(allowTelemetry(environment));
            operation.setRefreshInterval(getRefreshInterval(environment));
            operation.setUseCache(useCache(environment));

            environment.getPropertySources()
                    .addLast(new KeyVaultPropertySource(operation, environment.getProperty(AZURE_KEYVAULT_NAME)));
            LOGGER.info("KeyVaultPropertySource registered.");
        }
    }

    private static boolean shouldAddKeyVaultPropertySource(ConfigurableEnvironment environment) {
        return keyVaultEnabled(environment) && keyVaultPropertiesProvided(environment) && isKeyVaultClientAvailable();
    }

    private static boolean keyVaultEnabled(ConfigurableEnvironment environment) {
        return environment.getProperty(AZURE_KEY_VAULT_ENABLED, Boolean.class, true);
    }

    private static boolean keyVaultPropertiesProvided(ConfigurableEnvironment environment) {
        return AZURE_KEY_VAULT_PROPERTIES.stream().allMatch(environment::containsProperty);
    }

    private static boolean isKeyVaultClientAvailable() {
        return ClassUtils.isPresent("com.microsoft.azure.keyvault.KeyVaultClient",
                KeyVaultEnvironmentPostProcessor.class.getClassLoader());
    }

    private static boolean allowTelemetry(final ConfigurableEnvironment env) {
        return env.getProperty(AZURE_KEYVAULT_ALLOW_TELEMETRY, Boolean.class, true);
    }

    private static long getRefreshInterval(final ConfigurableEnvironment env) {
        return env.getProperty(AZURE_KEYVAULT_REFRESH_INTERVAL_MS, Long.class, DEFAULT_REFRESH_INTERVAL_MS);
    }

    private static boolean useCache(final ConfigurableEnvironment env) {
        return env.getProperty(AZURE_KEYVAULT_USE_CACHE, Boolean.class, true);
    }
}
