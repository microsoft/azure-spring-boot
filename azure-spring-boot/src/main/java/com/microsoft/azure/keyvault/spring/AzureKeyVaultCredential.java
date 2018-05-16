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

import java.net.MalformedURLException;
import java.util.concurrent.*;


public class AzureKeyVaultCredential extends KeyVaultCredentials {
    private static final long DEFAULT_TOKEN_ACQUIRE_TIMEOUT_IN_SECONDS = 60L;
    private String clientId;
    private String clientKey;
    private long timeoutInSeconds;

    public AzureKeyVaultCredential(String clientId, String clientKey, long timeoutInSeconds) {
        this.clientId = clientId;
        this.clientKey = clientKey;
        this.timeoutInSeconds = timeoutInSeconds;
    }

    public AzureKeyVaultCredential(String clientId, String clientKey) {
        this(clientId, clientKey, DEFAULT_TOKEN_ACQUIRE_TIMEOUT_IN_SECONDS);
    }

    @Override
    public String doAuthenticate(String authorization, String resource, String scope) {
        AuthenticationContext context = null;
        AuthenticationResult result = null;
        String token = "";
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        try {
            context = new AuthenticationContext(authorization, false, executorService);
            final ClientCredential credential = new ClientCredential(this.clientId, this.clientKey);

            final Future<AuthenticationResult> future = context.acquireToken(resource, credential, null);
            result = future.get(timeoutInSeconds, TimeUnit.SECONDS);
            token = result.getAccessToken();
        } catch (MalformedURLException | TimeoutException | InterruptedException | ExecutionException ex) {
            throw new IllegalStateException("Failed to do authentication.", ex);
        } finally {
            executorService.shutdown();
        }
        return token;
    }
}
