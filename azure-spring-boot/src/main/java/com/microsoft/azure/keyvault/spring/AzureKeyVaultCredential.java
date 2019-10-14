/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.keyvault.spring;

import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.azure.keyvault.authentication.KeyVaultCredentials;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.atomic.AtomicLong;


public class AzureKeyVaultCredential extends KeyVaultCredentials {
    private static final long DEFAULT_TOKEN_ACQUIRE_TIMEOUT_IN_SECONDS = 60L;
    private String clientId;
    private String clientKey;
    private long timeoutInSeconds;

    private AADAuthUtil aadAuthUtil;
    private String token = "";
    private AtomicLong lastAcquireTokenTime = new AtomicLong();
    private AtomicLong expireIn = new AtomicLong();
    private static final long EXPIRE_BUFFER_TIME = 10 * 1000; //buffer time 10s

    public AzureKeyVaultCredential(String clientId, String clientKey, long timeoutInSeconds, AADAuthUtil aadAuthUtil) {
        this.clientId = clientId;
        this.clientKey = clientKey;
        this.timeoutInSeconds = timeoutInSeconds;
        this.aadAuthUtil = aadAuthUtil;
    }

    public AzureKeyVaultCredential(String clientId, String clientKey, long timeoutInSeconds) {
        this(clientId, clientKey, timeoutInSeconds, new AADAuthUtil());
    }

    public AzureKeyVaultCredential(String clientId, String clientKey) {
        this(clientId, clientKey, DEFAULT_TOKEN_ACQUIRE_TIMEOUT_IN_SECONDS);
    }

    @Override
    public String doAuthenticate(String authorization, String resource, String scope) {
        if (StringUtils.isEmpty(token) || needRefresh()) {
            refreshToken(authorization, resource);
        }
        return token;
    }

    private synchronized void refreshToken(String authorization, String resource) {
        if (!needRefresh()) { //double check
            return;
        }

        try {
            final AuthenticationResult result = aadAuthUtil.getToken(authorization,
                    resource,
                    clientId,
                    clientKey,
                    timeoutInSeconds);
            token = result.getAccessToken();
            expireIn.set(result.getExpiresAfter());
            lastAcquireTokenTime.set(System.currentTimeMillis());
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to do authentication.", ex);
        }
    }

    private boolean needRefresh() {
        return ((System.currentTimeMillis() - lastAcquireTokenTime.get() + EXPIRE_BUFFER_TIME) / 1000) >=
                expireIn.get();
    }
}
