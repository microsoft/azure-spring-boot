package com.microsoft.azure.keyvault.spring.boot;

import com.microsoft.azure.PagedList;
import com.microsoft.azure.keyvault.KeyVaultClient;
import com.microsoft.azure.keyvault.models.SecretBundle;
import com.microsoft.azure.keyvault.models.SecretItem;

import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class KeyVaultOperation {
    private KeyVaultClient keyVaultClient;
    private String vaultUri;

    private ConcurrentHashMap<String, Object> propertyNamesHashMap;
    private AtomicLong lastUpdateTime = new AtomicLong();
    private final Object refreshLock = new Object();
    private static final long CACHE_REFRESH_INTERVAL_IN_MS = 1800000L; // 30 minutes

    public KeyVaultOperation(KeyVaultClient keyVaultClient, String vaultUri) {
        this.keyVaultClient = keyVaultClient;
        this.vaultUri = vaultUri;
        if (vaultUri.endsWith("/")) {
            this.vaultUri = vaultUri.substring(0, vaultUri.length() - 1);
        }
        createOrUpdateHashMap();
        lastUpdateTime.set(System.currentTimeMillis());
    }

    public String[] list() {
        return Collections.list(propertyNamesHashMap.keys()).toArray(new String[propertyNamesHashMap.size()]);
    }

    public Object get(String secretName) {
        // NOTE: azure keyvault secret name convention: ^[0-9a-zA-Z-]+$ "." is not allowed
        secretName = secretName.replace(".", "-");

        // refresh periodically
        if (System.currentTimeMillis() - lastUpdateTime.get() > CACHE_REFRESH_INTERVAL_IN_MS) {
            synchronized (refreshLock) {
                if (System.currentTimeMillis() - lastUpdateTime.get() > CACHE_REFRESH_INTERVAL_IN_MS) {
                    lastUpdateTime.set(System.currentTimeMillis());
                    // refresh propertyNames
                    createOrUpdateHashMap();
                }
            }
        }

        if (propertyNamesHashMap.containsKey(secretName)) {
            final SecretBundle secretBundle = keyVaultClient.getSecret(vaultUri, secretName);
            return secretBundle.value();

        } else {
            return null;
        }
    }

    private void createOrUpdateHashMap() {
        if (propertyNamesHashMap == null) {
            propertyNamesHashMap = new ConcurrentHashMap<String, Object>();
        }

        final PagedList<SecretItem> secrets = keyVaultClient.listSecrets(vaultUri);
        for (final SecretItem secret : secrets) {
            propertyNamesHashMap.putIfAbsent(secret.id().replaceFirst(vaultUri + "/secrets/", ""), secret.id());
        }
    }
}
