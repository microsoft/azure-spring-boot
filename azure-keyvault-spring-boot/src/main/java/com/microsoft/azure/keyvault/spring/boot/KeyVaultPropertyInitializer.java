package com.microsoft.azure.keyvault.spring.boot;

import com.microsoft.azure.keyvault.KeyVaultClient;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.util.Assert;

public class KeyVaultPropertyInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext ctx) {

        final ConfigurableEnvironment env = ctx.getEnvironment();

        final String clientId = getProperty(env, Constants.AZURE_CLIENTID);
        final String clientKey = getProperty(env, Constants.AZURE_CLIENTKEY);
        final String vaultUri = getProperty(env, Constants.AZURE_KEYVAULT_VAULT_URI);
        boolean enabled = true;

        if (env.getProperty(Constants.AZURE_KEYVAULT_ENABLED) != null) {
            enabled = Boolean.parseBoolean(env.getProperty(Constants.AZURE_KEYVAULT_ENABLED));
        }
        env.getProperty(Constants.AZURE_KEYVAULT_ENABLED, boolean.class);

        if (enabled) {
            final KeyVaultClient kvClient = new KeyVaultClient(
                    new AzureKeyVaultCredential(clientId, clientKey));

            try {
                final MutablePropertySources sources = env.getPropertySources();
                sources.addFirst(new KeyVaultPropertySource(new KeyVaultOperation(kvClient, vaultUri)));

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
            throw new IllegalArgumentException("property " + propertyName + " must not null!");
        }
        return property;
    }
}
