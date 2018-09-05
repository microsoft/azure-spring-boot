/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.keyvault.spring;

import com.microsoft.azure.keyvault.KeyVaultClient;
import com.microsoft.azure.keyvault.spring.secrets.KeyVaultSecretOperation;
import com.microsoft.azure.keyvault.spring.secrets.KeyVaultSecretTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(KeyVaultClient.class)
@ConditionalOnProperty(prefix = "azure.keyvault", value = {"name", "client-id", "client-key"})
@EnableConfigurationProperties(KeyVaultProperties.class)
public class KeyVaultAutoConfiguration {
    private final KeyVaultProperties properties;

    public KeyVaultAutoConfiguration(KeyVaultProperties properties) {
        this.properties = properties;
    }

    @Bean
    public KeyVaultSecretOperation getSecretOperation() {
        return new KeyVaultSecretTemplate(properties.getClientId(), properties.getClientKey(),
                properties.getRefreshInterval());
    }
}
