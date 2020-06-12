/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.keyvault.spring;

import static com.microsoft.azure.keyvault.spring.Constants.AZURE_KEYVAULT_PROPERTYSOURCE_NAME;

import org.springframework.core.env.EnumerablePropertySource;

public class KeyVaultPropertySource extends EnumerablePropertySource<KeyVaultOperation> {

    private final KeyVaultOperation operations;

    public KeyVaultPropertySource(String keyVaultName, KeyVaultOperation operation) {
        super(keyVaultName, operation);
        this.operations = operation;
    }

    public KeyVaultPropertySource(KeyVaultOperation operation) {
        super(AZURE_KEYVAULT_PROPERTYSOURCE_NAME, operation);
        this.operations = operation;
    }

    @Override
    public String[] getPropertyNames() {
        return this.operations.getPropertyNames();
    }

    public Object getProperty(String name) {
        return operations.get(name);
    }
}
