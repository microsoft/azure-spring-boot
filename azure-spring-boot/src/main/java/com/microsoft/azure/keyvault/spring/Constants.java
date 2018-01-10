/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.keyvault.spring;

public class Constants {
    // TODO (wepa) Load service version dynamically
    public static final String AZURE_KEYVAULT_USER_AGENT = "spring-boot-starter/0.2.2-SNAPSHOT";
    public static final String AZURE_CLIENTID = "azure.keyvault.client-id";
    public static final String AZURE_CLIENTKEY = "azure.keyvault.client-key";
    public static final String AZURE_KEYVAULT_ENABLED = "azure.keyvault.enabled";
    public static final String AZURE_KEYVAULT_VAULT_URI = "azure.keyvault.uri";
    public static final String AZURE_KEYVAULT_PROPERTYSOURCE_NAME = "azurekv";
    public static final String AZURE_TOKEN_ACQUIRE_TIMEOUT_IN_SECONDS = "azure.keyvault.token-acquire-timeout-seconds";
    public static final String AZURE_KEYVAULT_ALLOW_TELEMETRY = "azure.keyvault.allow.telemetry";
}
