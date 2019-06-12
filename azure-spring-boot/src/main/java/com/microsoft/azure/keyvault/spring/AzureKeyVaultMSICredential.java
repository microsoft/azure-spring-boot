/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.keyvault.spring;

import com.microsoft.azure.AzureEnvironment;
import com.microsoft.azure.credentials.AzureTokenCredentials;
import com.microsoft.azure.credentials.MSICredentials;
import com.microsoft.azure.keyvault.authentication.KeyVaultCredentials;

import java.io.IOException;

public class AzureKeyVaultMSICredential extends KeyVaultCredentials {
    private final AzureTokenCredentials tokenCredentials;

    public AzureKeyVaultMSICredential(AzureTokenCredentials tokenCredentials) {
        this.tokenCredentials = tokenCredentials;
    }

    public AzureKeyVaultMSICredential(AzureEnvironment environment) {
        this.tokenCredentials = new MSICredentials(environment);
    }

    public AzureKeyVaultMSICredential(AzureEnvironment environment, String clientId) {
        this.tokenCredentials = new MSICredentials(environment).withClientId(clientId);
    }

    @Override
    public String doAuthenticate(String authorization, String resource, String scope) {
        try {
            return tokenCredentials.getToken(resource);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to do authentication.", e);
        }
    }
}
