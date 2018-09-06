/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.keyvault.spring;

import java.util.List;

/**
 * Azure Key Vault related operation
 */
public interface KeyVaultOperation {
    String getSecret(String keyVaultName, String secretName);

    List<String> listSecrets(String keyVaultName);
}
