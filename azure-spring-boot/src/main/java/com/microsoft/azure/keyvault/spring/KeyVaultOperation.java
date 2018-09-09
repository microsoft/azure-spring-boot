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
    private final long cacheRefreshIntervalInMs;
    private final Object refreshLock = new Object();
    private final KeyVaultClient keyVaultClient;
    private final String vaultUri;
    private ConcurrentHashMap<String, Object> propertyNamesHashMap;
    private final AtomicLong lastUpdateTime = new AtomicLong();
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    public KeyVaultOperation(final KeyVaultClient keyVaultClient, final String vaultUri) {
        this(keyVaultClient, vaultUri, Constants.DEFAULT_REFRESH_INTERVAL_MS);
    }

    public KeyVaultOperation(final KeyVaultClient keyVaultClient, String vaultUri, final long refreshInterval) {
        this.cacheRefreshIntervalInMs = refreshInterval;
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
            this.rwLock.readLock().lock();
            return Collections.list(this.propertyNamesHashMap.keys())
                    .toArray(new String[this.propertyNamesHashMap.size()]);
        } finally {
            this.rwLock.readLock().unlock();
        }
    }

    public Object get(final String secretName) {
        // NOTE: azure keyvault secret name convention: ^[0-9a-zA-Z-]+$ "." is not allowed
        final String localSecretName = secretName.replace(".", "-");

        // refresh periodically
        if (System.currentTimeMillis() - this.lastUpdateTime.get() > this.cacheRefreshIntervalInMs) {
            synchronized (this.refreshLock) {
                if (System.currentTimeMillis() - this.lastUpdateTime.get() > this.cacheRefreshIntervalInMs) {
                    this.lastUpdateTime.set(System.currentTimeMillis());
                    createOrUpdateHashMap();
                }
            }
        }

        if (this.propertyNamesHashMap.containsKey(secretName)) {
            final SecretBundle secretBundle = this.keyVaultClient.getSecret(this.vaultUri, localSecretName);
            return secretBundle.value();

        } else {
            return null;
        }
    }

    private void createOrUpdateHashMap() {
        if (this.propertyNamesHashMap == null) {
            this.propertyNamesHashMap = new ConcurrentHashMap<>();
        }

        try {
            this.rwLock.writeLock().lock();
            this.propertyNamesHashMap.clear();

            final PagedList<SecretItem> secrets = this.keyVaultClient.listSecrets(this.vaultUri);
            secrets.loadAll();

            for (final SecretItem secret : secrets) {
                this.propertyNamesHashMap.putIfAbsent(secret.id()
                        .replaceFirst(this.vaultUri + "/secrets/", "")
                        .replaceAll("-", "."), secret.id());
                this.propertyNamesHashMap.putIfAbsent(secret.id()
                        .replaceFirst(this.vaultUri + "/secrets/", ""), secret.id());
            }

            this.lastUpdateTime.set(System.currentTimeMillis());
        } finally {
            this.rwLock.writeLock().unlock();
        }
    }
}
