/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.keyvault.spring.secrets;

import com.microsoft.azure.keyvault.spring.KeyVaultOperation;

import java.util.List;

/**
 * Azure Key Vault Secrets related operation
 */
public interface KeyVaultSecretOperation extends KeyVaultOperation {
    String getSecret(String keyVaultName, String secretName);

    List<String> listSecrets(String keyVaultName);
}
