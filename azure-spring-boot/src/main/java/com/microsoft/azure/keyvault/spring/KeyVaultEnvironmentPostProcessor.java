/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.keyvault.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.ClassUtils;

@Slf4j
public class KeyVaultEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {
    public static final int DEFAULT_ORDER = ConfigFileApplicationListener.DEFAULT_ORDER + 1;
    private int order = DEFAULT_ORDER;

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (isKeyVaultEnabled(environment)) {
            final KeyVaultEnvironmentPostProcessorHelper helper =
                    new KeyVaultEnvironmentPostProcessorHelper(environment);
            helper.addKeyVaultPropertySource();
        }
    }

    private boolean isKeyVaultEnabled(ConfigurableEnvironment environment) {
        return environment.getProperty(Constants.AZURE_KEYVAULT_ENABLED, Boolean.class, true)
                && environment.getProperty(Constants.AZURE_KEYVAULT_VAULT_URI) != null && isKeyVaultClientAvailable();
    }

    private boolean isKeyVaultClientAvailable() {
        return ClassUtils.isPresent("com.azure.security.keyvault.secrets.SecretClient",
                KeyVaultEnvironmentPostProcessor.class.getClassLoader());
    }

    @Override
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
