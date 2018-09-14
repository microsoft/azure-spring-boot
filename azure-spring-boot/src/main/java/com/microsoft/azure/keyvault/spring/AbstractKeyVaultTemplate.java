/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.keyvault.spring;

import com.microsoft.azure.AzureResponseBuilder;
import com.microsoft.azure.keyvault.KeyVaultClient;
import com.microsoft.azure.serializer.AzureJacksonAdapter;
import com.microsoft.azure.spring.support.UserAgent;
import com.microsoft.azure.utils.Memoizer;
import com.microsoft.rest.RestClient;
import com.microsoft.rest.credentials.ServiceClientCredentials;

import java.util.function.Function;

import static com.microsoft.azure.keyvault.spring.Constants.DEFAULT_REFRESH_INTERVAL_MS;

public abstract class AbstractKeyVaultTemplate implements KeyVaultOperation {
    private static final String KEY_VAULT_URL = "https://%s.vault.azure.net/";

    protected final ServiceClientCredentials credential;
    protected final Function<String, KeyVaultClient> keyVaultClientCreator = keyVaultClientCreator();
    private boolean allowTelemetry = false;

    public AbstractKeyVaultTemplate(String clientId, String clientSecret) {
        this.credential = new AzureKeyVaultCredential(clientId, clientSecret);
    }

    public void setAllowTelemetry(boolean allowTelemetry) {
        this.allowTelemetry = allowTelemetry;
    }

    private KeyVaultClient buildKeyVaultClient(String keyVaultName) {
        final String keyVaultUrl = buildKeyVaultUrl(keyVaultName);
        final RestClient restClient = new RestClient.Builder().withBaseUrl(keyVaultUrl).withCredentials(this.credential)
                .withSerializerAdapter(new AzureJacksonAdapter())
                .withResponseBuilderFactory(new AzureResponseBuilder.Factory())
                .withUserAgent(UserAgent.getUserAgent(Constants.AZURE_KEYVAULT_USER_AGENT, allowTelemetry)).build();

        return new KeyVaultClient(restClient);
    }

    protected static String buildKeyVaultUrl(String keyVaultName) {
        return String.format(KEY_VAULT_URL, keyVaultName);
    }

    private Function<String, KeyVaultClient> keyVaultClientCreator() {
        return Memoizer.memoize(this::buildKeyVaultClient);
    }
}
