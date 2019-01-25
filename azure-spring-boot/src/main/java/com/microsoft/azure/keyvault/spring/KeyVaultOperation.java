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
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class KeyVaultOperation {
    private final long cacheRefreshIntervalInMs;
    private final Object refreshLock = new Object();
    private final KeyVaultClient keyVaultClient;
    private final String vaultUri;
    private ConcurrentHashMap<String, Object> propertyNamesHashMap = new ConcurrentHashMap<>();
    private final AtomicLong lastUpdateTime = new AtomicLong();
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    public KeyVaultOperation(final KeyVaultClient keyVaultClient, final String vaultUri) {
        this(keyVaultClient, vaultUri, Constants.DEFAULT_REFRESH_INTERVAL_MS);
    }

    public KeyVaultOperation(final KeyVaultClient keyVaultClient, String vaultUri, final long refreshInterval) {
        this.cacheRefreshIntervalInMs = refreshInterval;
        this.keyVaultClient = keyVaultClient;
        // TODO(pan): need to validate why last '/' need to be truncated.
        this.vaultUri = StringUtils.trimTrailingCharacter(vaultUri.trim(), '/');

        fillSecretsHashMap();
    }

    public String[] list() {
        try {
            this.rwLock.readLock().lock();
            return Collections.list(propertyNamesHashMap.keys()).toArray(new String[propertyNamesHashMap.size()]);
        } finally {
            this.rwLock.readLock().unlock();
        }
    }

    private String getKeyvaultSecretName(@NonNull String property) {
        if (property.matches("[a-z0-9A-Z-]+")) {
            return property.toLowerCase(Locale.US);
        } else if (property.matches("[A-Z0-9_]+")) {
            return property.toLowerCase(Locale.US).replaceAll("_", "-");
        } else {
            return property.toLowerCase(Locale.US)
                    .replaceAll("-", "")     // my-project -> myproject
                    .replaceAll("_", "")     // my_project -> myproject
                    .replaceAll("\\.", "-"); // acme.myproject -> acme-myproject
        }
    }

    /**
     * For convention we need to support all relaxed binding format from spring, these may include:
     * <table>
     * <tr><td>Spring relaxed binding names</td></tr>
     * <tr><td>acme.my-project.person.first-name</td></tr>
     * <tr><td>acme.myProject.person.firstName</td></tr>
     * <tr><td>acme.my_project.person.first_name</td></tr>
     * <tr><td>ACME_MYPROJECT_PERSON_FIRSTNAME</td></tr>
     * </table>
     * But azure keyvault only allows ^[0-9a-zA-Z-]+$ and case insensitive, so there must be some conversion
     * between spring names and azure keyvault names.
     * For example, the 4 properties stated above should be convert to acme-myproject-person-firstname in keyvault.
     *
     * @param property of secret instance.
     * @return the value of secret with given name or null.
     */
    public String get(final String property) {
        Assert.hasText(property, "property should contain text.");
        final String secretName = getKeyvaultSecretName(property);

        // refresh periodically
        if (System.currentTimeMillis() - this.lastUpdateTime.get() > this.cacheRefreshIntervalInMs) {
            synchronized (this.refreshLock) {
                if (System.currentTimeMillis() - this.lastUpdateTime.get() > this.cacheRefreshIntervalInMs) {
                    this.lastUpdateTime.set(System.currentTimeMillis());
                    fillSecretsHashMap();
                }
            }
        }

        if (this.propertyNamesHashMap.containsKey(secretName)) {
            final SecretBundle secretBundle = this.keyVaultClient.getSecret(this.vaultUri, secretName);
            return secretBundle.value();
        } else {
            return null;
        }
    }

    private void fillSecretsHashMap() {
        try {
            this.rwLock.writeLock().lock();
            this.propertyNamesHashMap.clear();

            final PagedList<SecretItem> secrets = this.keyVaultClient.listSecrets(this.vaultUri);
            secrets.loadAll();

            secrets.forEach(s -> {
                final String secretName = s.id().replace(vaultUri + "/secrets/", "").toLowerCase(Locale.US);
                propertyNamesHashMap.putIfAbsent(secretName, s.id());
            });

            this.lastUpdateTime.set(System.currentTimeMillis());
        } finally {
            this.rwLock.writeLock().unlock();
        }
    }
}
