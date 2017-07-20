package com.microsoft.azure.keyvault.spring.boot;

import org.springframework.core.env.EnumerablePropertySource;

public class KeyVaultPropertySource extends EnumerablePropertySource {

    private final KeyVaultOperation operations;

    public KeyVaultPropertySource(KeyVaultOperation operations) {
        super(Constants.AZURE_KEYVAULT_PROERTYSOURCE_NAME, operations);
        this.operations = operations;
    }


    public String[] getPropertyNames() {
        return this.operations.list();
    }


    public Object getProperty(String name) {
        return operations.get(name);
    }
}
