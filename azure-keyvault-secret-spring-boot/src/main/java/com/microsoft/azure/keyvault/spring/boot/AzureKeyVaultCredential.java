package com.microsoft.azure.keyvault.spring.boot;

import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.ClientCredential;
import com.microsoft.azure.keyvault.authentication.KeyVaultCredentials;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


public class AzureKeyVaultCredential extends KeyVaultCredentials {
    private String clientId;
    private String clientKey;
    private long timeoutInSeconds;

    private static final long DEFAULT_TOKEN_ACQURING_TIMEOUT_IN_SECONDS = 60L;

    public AzureKeyVaultCredential(String clientId, String clientKey, long timeoutInSeconds) {
        this.clientId = clientId;
        this.clientKey = clientKey;
        this.timeoutInSeconds = timeoutInSeconds;
    }

    public AzureKeyVaultCredential(String clientId, String clientKey) {
        this(clientId, clientKey, DEFAULT_TOKEN_ACQURING_TIMEOUT_IN_SECONDS);
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

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            executorService.shutdown();
        }
        return token;
    }
}
