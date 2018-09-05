/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.keyvault.spring;

import com.microsoft.azure.utils.PropertyLoader;

public class Constants {
    public static final String AZURE_KEYVAULT_USER_AGENT = "spring-boot-starter/" + PropertyLoader.getProjectVersion();
    public static final String AZURE_KEYVAULT_PROPERTYSOURCE_NAME = "azurekv";

    public static final String KEY_VALUE_PREFIX = "keyVault:";

    public static final String AZURE_KEY_VAULT_ENABLED = "azure.keyvault.enabled";
    public static final String AZURE_CLIENT_ID = "azure.keyvault.client-id";
    public static final String AZURE_KEYVAULT_CLIENT_KEY = "azure.keyvault.client-key";
    public static final String AZURE_KEYVAULT_NAME = "azure.keyvault.name";
    public static final String AZURE_KEYVAULT_ALLOW_TELEMETRY = "azure.keyvault.allow-telemetry";
    public static final String AZURE_KEYVAULT_REFRESH_INTERVAL_MS = "azure.keyvault.refresh-interval";
    public static final String AZURE_KEYVAULT_USE_CACHE = "azure.keyvault.use-cache";

    public static final long DEFAULT_REFRESH_INTERVAL_MS = -1L; // Refresh disabled
}
