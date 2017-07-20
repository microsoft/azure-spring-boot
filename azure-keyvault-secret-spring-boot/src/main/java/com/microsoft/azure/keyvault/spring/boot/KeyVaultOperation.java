package com.microsoft.azure.keyvault.spring.boot;

import com.microsoft.azure.PagedList;
import com.microsoft.azure.keyvault.KeyVaultClient;
import com.microsoft.azure.keyvault.models.SecretBundle;
import com.microsoft.azure.keyvault.models.SecretItem;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class KeyVaultOperation {
    private KeyVaultClient keyVaultClient;
    private String vaultUri;
    private String[] propertyNames;

    public KeyVaultOperation(KeyVaultClient keyVaultClient, String vaultUri) {
        this.keyVaultClient = keyVaultClient;
        this.vaultUri = vaultUri;
    }

    public String[] list() {
        if (vaultUri.endsWith("/")) {
            vaultUri = vaultUri.substring(0, vaultUri.length() - 1);
        }

        if (propertyNames == null || propertyNames.length == 0) {
            final PagedList<SecretItem> secrets = keyVaultClient.listSecrets(vaultUri);
            final List<String> values = secrets.stream().map(s -> s.id()).map(
                    s -> s.replaceFirst(vaultUri + "/secrets/", "")).collect(Collectors.toList());

            propertyNames = values.stream().toArray(String[]::new);

        }
        return (String[]) this.propertyNames.clone();
    }

    public Object get(String secretName) {
        // NOTE: azure keyvault secret name convention: ^[0-9a-zA-Z-]+$ "." is not allowed
        secretName = secretName.replace(".", "-");
        if (propertyNames == null || propertyNames.length == 0) {
            this.list();
        }

        if (Arrays.asList(propertyNames).contains(secretName)) {
            final SecretBundle secret = keyVaultClient.getSecret(vaultUri, secretName);
            return secret.value();
        } else {
            return null;
        }
    }
}
