/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.keyvault.spring;

import com.microsoft.azure.keyvault.KeyVaultClient;

public class KeyVaultOperationFactory {
    public static KeyVaultOperation createDefaultKeyVault(final KeyVaultClient keyVaultClient, final String vaultUri) {
        return new KeyVaultOperation(keyVaultClient, vaultUri, Constants.DEFAULT_REFRESH_INTERVAL_MS, VaultPolicy.LIST);
    }
}
