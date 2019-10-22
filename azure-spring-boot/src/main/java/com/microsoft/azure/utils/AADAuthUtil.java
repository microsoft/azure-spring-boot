/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.utils;

import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.ClientCredential;

import java.net.MalformedURLException;
import java.util.concurrent.*;

public class AADAuthUtil {
    public AuthenticationResult getToken(String authorization,
                                         String resource,
                                         String clientId,
                                         String clientKey,
                                         long tokenAcquireTimeout) {
        AuthenticationContext context = null;
        AuthenticationResult result = null;
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        try {
            context = new AuthenticationContext(authorization, false, executorService);
            final ClientCredential credential = new ClientCredential(clientId, clientKey);

            final Future<AuthenticationResult> future = context.acquireToken(resource, credential, null);
            result = future.get(tokenAcquireTimeout, TimeUnit.SECONDS);
        } catch (MalformedURLException | TimeoutException | InterruptedException | ExecutionException ex) {
            throw new IllegalStateException("Failed to do authentication.", ex);
        } finally {
            executorService.shutdown();
        }
        return result;
    }
}
