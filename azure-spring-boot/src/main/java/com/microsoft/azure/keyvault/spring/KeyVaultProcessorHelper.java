/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.keyvault.spring;

import com.microsoft.azure.keyvault.KeyVaultClient;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;

public class KeyVaultProcessorHelper {
    private ConfigurableEnvironment environment;
    private String id;
    private String key;
    private String uri;
    private long timeout;

    public KeyVaultProcessorHelper(ConfigurableEnvironment environment,
                                   String id, String key, String uri, long timeout) {
        this.environment = environment;
        this.id = id;
        this.key = key;
        this.uri = uri;
        this.timeout = timeout;
    }

    public void addKeyVaultPropertySource() {
        final KeyVaultClient kvClient = new KeyVaultClient(
                new AzureKeyVaultCredential(id, key, timeout));

        try {
            final MutablePropertySources sources = environment.getPropertySources();
            final KeyVaultOperation kvOperation = new KeyVaultOperation(kvClient, uri);

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
