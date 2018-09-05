/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.keyvault.spring;

import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.ClientCredential;
import com.microsoft.azure.keyvault.authentication.KeyVaultCredentials;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Credential used for Key Vault call. This class just fetch token by provided
 * {@code clientId} and {@code clientSecret}. This should be provided by Azure SDK
 *
 * @author Warren Zhu
 */
@AllArgsConstructor
public class AzureKeyVaultCredential extends KeyVaultCredentials {
    private static final Logger LOGGER = LoggerFactory.getLogger(AzureKeyVaultCredential.class);
    private final String clientId;
    private final String clientSecret;

    @Override
    public String doAuthenticate(String authorization, String resource, String scope) {
        try {
            final AuthenticationContext context =
                    new AuthenticationContext(authorization, false, Executors.newSingleThreadExecutor());

            final Future<AuthenticationResult> future =
                    context.acquireToken(resource, new ClientCredential(this.clientId, this.clientSecret), null);
            return future.get().getAccessToken();
        } catch (MalformedURLException | InterruptedException | ExecutionException e) {
            LOGGER.error("Failed to do Azure Key Vault authentication.", e);
            throw new IllegalStateException("Failed to do Azure Key Vault authentication.", e);
        }
    }
}
