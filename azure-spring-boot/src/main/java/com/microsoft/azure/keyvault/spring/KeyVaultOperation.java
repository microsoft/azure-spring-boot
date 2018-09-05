/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.keyvault.spring;

/**
 * Azure Key Vault related operation
 */
public interface KeyVaultOperation {
    void setUseCache(boolean useCache);

    void setRefreshInterval(long refreshIntervalMilliSecs);

    void setAllowTelemetry(boolean allowTelemetry);
}
