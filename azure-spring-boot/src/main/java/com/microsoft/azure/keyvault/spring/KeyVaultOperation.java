/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.keyvault.spring;

import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class KeyVaultOperation {

    private final SecretClient keyVaultClient;
    private final String vaultUri;
    private final long cacheRefreshIntervalInMs;
    private final AtomicLong lastUpdateTime = new AtomicLong();

    private volatile Map<String, String> keyVaultItems;
    private volatile String[] propertyNames;

    public KeyVaultOperation(
            final SecretClient keyVaultClient,
            String vaultUri,
            final long cacheRefreshIntervalInMs,
            final List<String> secretKeys
    ) {
        this.cacheRefreshIntervalInMs = cacheRefreshIntervalInMs;
        this.propertyNames = (String[]) secretKeys.stream()
                .map(String::toLowerCase)
                .flatMap(name -> Stream.of(name, name.replaceAll("-", ".")))
                .distinct()
                .toArray();
        this.keyVaultClient = keyVaultClient;
        // TODO(pan): need to validate why last '/' need to be truncated.
        this.vaultUri = StringUtils.trimTrailingCharacter(vaultUri.trim(), '/');
        refreshKeyVaultItems();
    }

    public String[] getPropertyNames() {
        return propertyNames;
    }

    private String getKeyVaultSecretName(@NonNull String property) {
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
        refreshKeyVaultItemsIfNeeded();
        return Optional.of(property)
                .map(this::getKeyVaultSecretName)
                .map(keyVaultItems::get)
                .orElse(null);
    }

    private synchronized void refreshKeyVaultItemsIfNeeded() {
        if (needRefreshKeyVaultItems()) {
            refreshKeyVaultItems();
            this.lastUpdateTime.set(System.currentTimeMillis());
        }
    }

    private boolean needRefreshKeyVaultItems() {
        return (propertyNames == null || propertyNames.length == 0)
                && System.currentTimeMillis() - this.lastUpdateTime.get() > this.cacheRefreshIntervalInMs;
    }

    private synchronized void refreshKeyVaultItems() {
        if (this.propertyNames == null || propertyNames.length == 0) {
            propertyNames = (String[]) keyVaultClient.listPropertiesOfSecrets().stream()
                    .map(secretProperties -> secretProperties.getName().replace(vaultUri + "/secrets/", ""))
                    .map(String::toLowerCase)
                    .flatMap(name -> Stream.of(name, name.replaceAll("-", ".")))
                    .distinct()
                    .toArray();
        }
        keyVaultItems = Stream.of(propertyNames)
                .collect(Collectors.toMap(
                        name -> name,
                        this::getValueFromKeyVault
                ));
    }

    private String getValueFromKeyVault(String name) {
        return Optional.ofNullable(name)
                .map(keyVaultClient::getSecret)
                .map(KeyVaultSecret::getName)
                .orElse(null);
    }

}
