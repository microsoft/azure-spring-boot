/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.keyvault.spring.boot;

import org.springframework.core.env.EnumerablePropertySource;

public class KeyVaultPropertySource extends EnumerablePropertySource<KeyVaultOperation> {

    private final KeyVaultOperation operations;

    public KeyVaultPropertySource(KeyVaultOperation operations) {
        super(Constants.AZURE_KEYVAULT_PROPERTYSOURCE_NAME, operations);
        this.operations = operations;
    }


    public String[] getPropertyNames() {
        return this.operations.list();
    }


    public Object getProperty(String name) {
        return operations.get(name);
    }
}
