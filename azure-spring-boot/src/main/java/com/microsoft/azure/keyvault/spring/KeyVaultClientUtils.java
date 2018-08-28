package com.microsoft.azure.keyvault.spring;

import com.microsoft.azure.AzureResponseBuilder;
import com.microsoft.azure.keyvault.KeyVaultClient;
import com.microsoft.azure.serializer.AzureJacksonAdapter;
import com.microsoft.azure.spring.support.UserAgent;
import com.microsoft.rest.RestClient;
import com.microsoft.rest.credentials.ServiceClientCredentials;

public class KeyVaultClientUtils {
    public static KeyVaultClient getClient(KeyVaultProperties properties) {
        final String clientId = properties.getClientId();
        final String clientKey = properties.getClientKey();
        final String uri = properties.getUri();
        final long tokenAcquireTimeoutSecs = properties.getTokenAcquireTimeoutSeconds();
        final boolean allowTelemetry = properties.isAllowTelemetry();

        final ServiceClientCredentials credentials =
                new AzureKeyVaultCredential(clientId, clientKey, tokenAcquireTimeoutSecs);
        final RestClient restClient = new RestClient.Builder().withBaseUrl(uri)
                .withCredentials(credentials)
                .withSerializerAdapter(new AzureJacksonAdapter())
                .withResponseBuilderFactory(new AzureResponseBuilder.Factory())
                .withUserAgent(UserAgent.getUserAgent(Constants.AZURE_KEYVAULT_USER_AGENT, allowTelemetry))
                .build();

        return new KeyVaultClient(restClient);
    }
}
