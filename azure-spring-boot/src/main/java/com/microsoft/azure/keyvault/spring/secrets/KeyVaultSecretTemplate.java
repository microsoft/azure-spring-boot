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
import com.microsoft.azure.keyvault.spring.KeyVaultOperation;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Default implementation of {@link KeyVaultOperation}
 *
 */
public class KeyVaultSecretTemplate extends AbstractKeyVaultTemplate {
    private Map<String, Set<String>> secretNamesByKeyVault = new ConcurrentHashMap<>();
    private Map<Pair<String, String>, String> secretsByKeyVault = new ConcurrentHashMap<>();

    public KeyVaultSecretTemplate(String clientId, String clientSecret) {
        super(clientId, clientSecret);
    }

    public KeyVaultSecretTemplate(String clientId, String clientSecret, long refreshIntervalMS) {
        super(clientId, clientSecret);
        scheduleRefresh(refreshIntervalMS);
    }

    @Override
    public String getSecret(String keyVaultName, String secretName) {
        final Pair<String, String> keyVaultAndSecret = Pair.of(keyVaultName, secretName);
        this.secretsByKeyVault.computeIfAbsent(keyVaultAndSecret, this::fetchSecret);

        return this.secretsByKeyVault.get(keyVaultAndSecret);
    }

    @Override
    public List<String> listSecrets(String keyVaultName) {
        this.secretNamesByKeyVault.computeIfAbsent(keyVaultName, this::fetchSecretNames);

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

    private void scheduleRefresh(long refreshIntervalMS) {
        if (refreshIntervalMS <= 0) {
            // Do not refresh
            return;
        }

        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        final Runnable refresher = new Runnable() {
            @Override
            public void run() {
                final Set<String> keyVaultNames = secretNamesByKeyVault.keySet();
                secretsByKeyVault.clear();

                keyVaultNames.stream().forEach(keyVaultName -> {
                    final Set<String> secretNames = fetchSecretNames(keyVaultName);

                    secretNames.stream().forEach(secretName -> {
                        final Pair<String, String> keyVaultSecrect = Pair.of(keyVaultName, secretName);
                        final String secretValue = fetchSecret(keyVaultSecrect);

                        secretsByKeyVault.putIfAbsent(keyVaultSecrect, secretValue);
                    });
                });
            }
        };

        scheduler.scheduleWithFixedDelay(refresher, refreshIntervalMS, refreshIntervalMS, TimeUnit.MILLISECONDS);
    }
}
