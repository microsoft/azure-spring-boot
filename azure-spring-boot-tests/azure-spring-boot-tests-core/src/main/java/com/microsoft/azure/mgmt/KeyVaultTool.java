/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.mgmt;

import java.util.Random;

import com.microsoft.azure.management.keyvault.Vault;
import com.microsoft.azure.management.keyvault.Vaults;
import com.microsoft.azure.management.keyvault.implementation.KeyVaultManager;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;

public class KeyVaultTool {

    private Access access;
    private Vaults vaults;
    
    public KeyVaultTool(Access access) {
        this.access = access;
        vaults = KeyVaultManager
                .authenticate(access.credentials(), access.subscription())
                .vaults();
    }
    
    public Vault createVaultInNewGroup(String resourceGroup, String prefix) {
        final String vaultName = keyVaultName(prefix);
        
        Vault result = vaults
                .define(vaultName)
                .withRegion(Region.US_WEST)
                .withNewResourceGroup(resourceGroup)
                .withEmptyAccessPolicy()
                .create();
        
        result = result
                .update()
                .defineAccessPolicy()
                .forServicePrincipal(access.servicePrincipal())
                .allowKeyAllPermissions()
                .allowSecretAllPermissions()
                .allowStorageAllPermissions()
                .attach()
                .apply();

        return result;
    }
    
    private String keyVaultName(String prefix) {
        String name;
        do {
            name = String.format("%s-%s", prefix, randomText(4));
        } while (!vaults.checkNameAvailability(name).nameAvailable());
        
        return name;
    }
    
    private String randomText(int size) {
        final Random random = new Random();
        
        final StringBuilder result = random
                .ints()
                .mapToObj(value -> (char) value)
                .filter(value -> value >= 'a' && value <= 'z')
                .limit(size)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append);

        return result.toString();
    }
}
