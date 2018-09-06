/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.keyvault.spring.secrets;

import com.microsoft.azure.PagedList;
import com.microsoft.azure.keyvault.models.SecretBundle;
import com.microsoft.azure.keyvault.models.SecretItem;
import com.microsoft.azure.keyvault.spring.AbstractKeyVaultTemplate;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Default implementation of {@link KeyVaultSecretOperation}
 *
 */
public class KeyVaultSecretTemplate extends AbstractKeyVaultTemplate implements KeyVaultSecretOperation {
    private final Map<String, Set<String>> secretNamesByKeyVault = new ConcurrentHashMap<>();
    private final Map<Pair<String, String>, String> secretsByKeyVault = new ConcurrentHashMap<>();

    private long lastUpdatedTime = System.currentTimeMillis();

    public KeyVaultSecretTemplate(String clientId, String clientSecret) {
        super(clientId, clientSecret);
    }

    public KeyVaultSecretTemplate(String clientId, String clientSecret, long refreshIntervalMS) {
        super(clientId, clientSecret, refreshIntervalMS);
    }

    @Override
    public String getSecret(String keyVaultName, String secretName) {
        final Pair<String, String> keyVaultAndSecret = Pair.of(keyVaultName, secretName);
        if (useCache) {
            clearCacheAfterInterval();
            this.secretsByKeyVault.computeIfAbsent(keyVaultAndSecret, this::fetchSecret);
        } else {
            this.secretsByKeyVault.put(keyVaultAndSecret, fetchSecret(keyVaultAndSecret));
        }

        return this.secretsByKeyVault.get(keyVaultAndSecret);
    }

    @Override
    public List<String> listSecrets(String keyVaultName) {
        if (useCache) {
            clearCacheAfterInterval();
            this.secretNamesByKeyVault.computeIfAbsent(keyVaultName, this::fetchSecretNames);
        } else {
            this.secretNamesByKeyVault.put(keyVaultName, fetchSecretNames(keyVaultName));
        }

        return new ArrayList<>(this.secretNamesByKeyVault.get(keyVaultName));
    }

    private Set<String> fetchSecretNames(String keyVaultName) {
        final String keyVaultUrl = buildKeyVaultUrl(keyVaultName);
        final PagedList<SecretItem> secretItems =
                this.keyVaultClientCreator.apply(keyVaultName).listSecrets(keyVaultUrl);

        return Collections.unmodifiableSet(
                secretItems.stream().map(SecretItem::id).map(s -> s.substring(s.lastIndexOf('/') + 1))
                        .collect(Collectors.toSet()));
    }

    private String fetchSecret(Pair<String, String> keyVaultAndSecret) {
        final String keyVaultName = keyVaultAndSecret.getLeft();

        final SecretBundle bundle = this.keyVaultClientCreator.apply(keyVaultName)
                .getSecret(buildKeyVaultUrl(keyVaultName), keyVaultAndSecret.getRight());

        return bundle == null ? null : bundle.value();
    }

    private void clearCacheAfterInterval() {
        if (refreshIntervalMS <= 0) {
            // Do not refresh
            return;
        }

        final long now = System.currentTimeMillis();
        if (now - this.lastUpdatedTime > refreshIntervalMS) {
            this.secretNamesByKeyVault.clear();
            this.secretsByKeyVault.clear();

            this.lastUpdatedTime = System.currentTimeMillis();
        }
    }
}
