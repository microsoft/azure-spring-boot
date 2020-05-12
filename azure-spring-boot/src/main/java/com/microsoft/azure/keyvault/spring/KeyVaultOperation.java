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

import java.util.ArrayList;
import java.util.Collection;
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
    private volatile List<String> propertyNames;

    public KeyVaultOperation(
            final SecretClient keyVaultClient,
            String vaultUri,
            final long cacheRefreshIntervalInMs,
            final List<String> secretKeys
    ) {
        this.keyVaultClient = keyVaultClient;
        // TODO(pan): need to validate why last '/' need to be truncated.
        this.vaultUri = StringUtils.trimTrailingCharacter(vaultUri.trim(), '/');
        this.cacheRefreshIntervalInMs = cacheRefreshIntervalInMs;
        this.propertyNames = Optional.ofNullable(secretKeys)
                .map(Collection::stream)
                .orElseGet(Stream::empty)
                .map(this::toUniformedPropertyName)
                .distinct()
                .collect(Collectors.toList());
        refreshKeyVaultItems();
    }

    public String[] getPropertyNames() {
        return propertyNames.toArray(new String[0]);
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
    private String toUniformedPropertyName(@NonNull String property) {
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

    public String get(final String property) {
        Assert.hasText(property, "property should contain text.");
        refreshKeyVaultItemsIfNeeded();
        return Optional.of(property)
                .map(this::toUniformedPropertyName)
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
        return (propertyNames == null || propertyNames.isEmpty())
                && System.currentTimeMillis() - this.lastUpdateTime.get() > this.cacheRefreshIntervalInMs;
    }

    private synchronized void refreshKeyVaultItems() {
        if (propertyNames == null || propertyNames.isEmpty()) {
            propertyNames = Optional.of(keyVaultClient)
                    .map(SecretClient::listPropertiesOfSecrets)
                    .map(secretProperties -> {
                        final List<String> secretNameList = new ArrayList<>();
                        secretProperties.forEach(s -> {
                            final String secretName = s.getName().replace(vaultUri + "/secrets/", "");
                            secretNameList.add(secretName);
                        });
                        return secretNameList;
                    })
                    .map(Collection::stream)
                    .orElseGet(Stream::empty)
                    .map(this::toUniformedPropertyName)
                    .distinct()
                    .collect(Collectors.toList());
        }
        keyVaultItems = Optional.ofNullable(propertyNames)
                .map(Collection::stream)
                .orElseGet(Stream::empty)
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
