package com.microsoft.azure.keyvault.spring.boot;

import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.ClientCredential;
import com.microsoft.azure.keyvault.authentication.KeyVaultCredentials;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


public class AzureKeyVaultCredential extends KeyVaultCredentials {
    private String clientId;
    private String clientKey;

    private static final Integer TOKEN_TIMEOUT = 3;

    public AzureKeyVaultCredential(String clientId, String clientKey) {
        this.clientId = clientId;
        this.clientKey = clientKey;
    }

    @Override
    public String doAuthenticate(String authorization, String resource, String scope) {
        AuthenticationContext context = null;
        AuthenticationResult result = null;
        String token = "";

        try {
            context = new AuthenticationContext(authorization, false, Executors.newSingleThreadExecutor());
            final ClientCredential credential = new ClientCredential(this.clientId, this.clientKey);

            final Future<AuthenticationResult> future = context.acquireToken(resource, credential, null);
            result = future.get(TOKEN_TIMEOUT, TimeUnit.MINUTES);
            token = result.getAccessToken();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return token;
    }
}
