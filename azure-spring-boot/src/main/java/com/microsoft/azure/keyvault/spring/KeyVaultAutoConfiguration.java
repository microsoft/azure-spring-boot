package com.microsoft.azure.keyvault.spring;

import com.microsoft.azure.keyvault.KeyVaultClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

@Configuration
@ConditionalOnClass(KeyVaultClient.class)
@ConditionalOnProperty(prefix = "azure.keyvault", value = {"uri", "client-id", "client-key"})
@EnableConfigurationProperties(KeyVaultProperties.class)
public class KeyVaultAutoConfiguration {
    private final KeyVaultProperties keyVaultProperties;

    public KeyVaultAutoConfiguration(KeyVaultProperties keyVaultProperties) {
        this.keyVaultProperties = keyVaultProperties;
    }

    @Bean
    public KeyVaultClient getKeyVaultClient() {
        return KeyVaultClientUtils.getClient(keyVaultProperties);
    }

    @Bean
    public KeyVaultOperation getKeyVaultOperation() {
        return new KeyVaultOperation(getKeyVaultClient(), keyVaultProperties.getUri());
    }
}
