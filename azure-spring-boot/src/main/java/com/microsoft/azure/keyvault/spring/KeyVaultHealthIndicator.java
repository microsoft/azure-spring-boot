/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.keyvault.spring;

import java.util.Iterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

/**
 * The custom Azure Key Vault /health indicator.
 */
@Component
public class KeyVaultHealthIndicator implements HealthIndicator {

    /**
     * Stores the environment we need.
     */
    @Autowired
    private ConfigurableEnvironment environment;

    /**
     * Constructor.
     */
    public KeyVaultHealthIndicator() {
    }

    /**
     * Provide the health of the KeyVault connection.
     *
     * @return the health.
     */
    @Override
    public Health health() {
        Health result = Health.down().build();
        boolean up = true;
        final Iterator<PropertySource<?>> iterator = environment.getPropertySources().iterator();
        while (iterator.hasNext()) {
            final PropertySource<?> propertySource = iterator.next();
            if (propertySource instanceof KeyVaultPropertySource) {
                final KeyVaultPropertySource keyVaultPS = (KeyVaultPropertySource) propertySource;
                if (!keyVaultPS.isUp()) {
                    up = false;
                    break;
                }
            }
        }
        if (up) {
            result = Health.up().build();
        }
        return result;
    }
}
