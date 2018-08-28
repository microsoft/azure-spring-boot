/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.keyvault.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.ClassUtils;

public class KeyVaultEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {

        if (isKeyVaultEnabled(environment)) {
            final KeyVaultEnvironmentPostProcessorHelper helper =
                    new KeyVaultEnvironmentPostProcessorHelper(environment);
            helper.addKeyVaultPropertySource();
        }
    }

    private boolean isKeyVaultEnabled(ConfigurableEnvironment environment) {
        if (environment.getProperty(Constants.AZURE_CLIENTID) == null) {
            // User doesn't want to enable Key Vault property initializer.
            return false;
        }
        return environment.getProperty(Constants.AZURE_KEYVAULT_ENABLED, Boolean.class, true)
                && isKeyVaultClientAvailable();
    }

    private boolean isKeyVaultClientAvailable() {
        return ClassUtils.isPresent("com.microsoft.azure.keyvault.KeyVaultClient",
                KeyVaultEnvironmentPostProcessor.class.getClassLoader());
    }
}
