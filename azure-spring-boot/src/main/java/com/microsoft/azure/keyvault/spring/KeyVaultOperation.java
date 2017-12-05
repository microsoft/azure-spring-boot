/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.keyvault.spring;

import com.microsoft.azure.PagedList;
import com.microsoft.azure.keyvault.KeyVaultClient;
import com.microsoft.azure.keyvault.models.SecretBundle;
import com.microsoft.azure.keyvault.models.SecretItem;

import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class KeyVaultOperation {
    private static final long CACHE_REFRESH_INTERVAL_IN_MS = 1800000L; // 30 minutes
    private final Object refreshLock = new Object();
    private KeyVaultClient keyVaultClient;
    private String vaultUri;
    private ConcurrentHashMap<String, Object> propertyNamesHashMap;
    private AtomicLong lastUpdateTime = new AtomicLong();
    private ReadWriteLock rwLock = new ReentrantReadWriteLock();

    public KeyVaultOperation(KeyVaultClient keyVaultClient, String vaultUri) {
        this.keyVaultClient = keyVaultClient;

        vaultUri = vaultUri.trim();
        if (vaultUri.endsWith("/")) {
            vaultUri = vaultUri.substring(0, vaultUri.length() - 1);
        }
        this.vaultUri = vaultUri;

        createOrUpdateHashMap();
    }

    public String[] list() {
        try {
            rwLock.readLock().lock();
            return Collections.list(propertyNamesHashMap.keys()).toArray(new String[propertyNamesHashMap.size()]);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public Object get(String secretName) {
        // NOTE: azure keyvault secret name convention: ^[0-9a-zA-Z-]+$ "." is not allowed
        final String localSecretName = secretName.replace(".", "-");

        // refresh periodically
        if (System.currentTimeMillis() - lastUpdateTime.get() > CACHE_REFRESH_INTERVAL_IN_MS) {
            synchronized (refreshLock) {
                if (System.currentTimeMillis() - lastUpdateTime.get() > CACHE_REFRESH_INTERVAL_IN_MS) {
                    lastUpdateTime.set(System.currentTimeMillis());
                    createOrUpdateHashMap();
                }
            }
        }

        if (propertyNamesHashMap.containsKey(secretName)) {
            final SecretBundle secretBundle = keyVaultClient.getSecret(vaultUri, localSecretName);
            return secretBundle.value();

        } else {
            return null;
        }
    }

    private void createOrUpdateHashMap() {
        if (propertyNamesHashMap == null) {
            propertyNamesHashMap = new ConcurrentHashMap<String, Object>();
        }

        try {
            rwLock.writeLock().lock();
            propertyNamesHashMap.clear();


            final PagedList<SecretItem> secrets = keyVaultClient.listSecrets(vaultUri);
            secrets.loadAll();
            for (final SecretItem secret : secrets) {
                propertyNamesHashMap.putIfAbsent(secret.id().replaceFirst(vaultUri + "/secrets/", "")
                        .replaceAll("-", "."), secret.id());
                propertyNamesHashMap.putIfAbsent(secret.id().replaceFirst(vaultUri + "/secrets/", ""), secret.id());
            }
            lastUpdateTime.set(System.currentTimeMillis());
        } finally {
            rwLock.writeLock().unlock();
        }
    }
}
