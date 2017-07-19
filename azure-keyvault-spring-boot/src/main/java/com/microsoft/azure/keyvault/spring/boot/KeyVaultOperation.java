package com.microsoft.azure.keyvault.spring.boot;

import com.microsoft.azure.PagedList;
import com.microsoft.azure.keyvault.KeyVaultClient;
import com.microsoft.azure.keyvault.models.SecretBundle;
import com.microsoft.azure.keyvault.models.SecretItem;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class KeyVaultOperation {
    private KeyVaultClient client;
    private String baseUri;
    private String[] propertyNames;

    public KeyVaultOperation(KeyVaultClient kvClient, String keyVaultBaseUri) {
        this.client = kvClient;
        this.baseUri = keyVaultBaseUri;
    }

    public String[] list() {
        if (baseUri.endsWith("/")) {
            baseUri = baseUri.substring(0, baseUri.length() - 1);
        }

        if (propertyNames == null || propertyNames.length == 0) {
            try {
                final PagedList<SecretItem> secrets = client.listSecrets(baseUri);
                final List<String> values = secrets.stream().map(s -> s.id()).map(
                        s -> s.replaceFirst(baseUri + "/secrets/", "")).collect(Collectors.toList());

                propertyNames = values.stream().toArray(String[]::new);
            } catch (Exception e) {
                e.printStackTrace();
                return new String[0];
            }
        }
        return (String[]) this.propertyNames.clone();
    }

    public Object get(String secretName) {
        // NOTE: azure keyvault secret name convention: [0-9a-zA-Z-]+$. "." is not allowed
        secretName = secretName.replace(".", "-");
        try {
            if (propertyNames == null || propertyNames.length == 0) {
                this.list();
            }

            if (Arrays.asList(propertyNames).contains(secretName)) {
                final SecretBundle secret = client.getSecret(baseUri, secretName);
                return secret.value();
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
