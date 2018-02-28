/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.autoconfigure.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("azure.storage")
public class StorageProperties {

    /**
     * Azure Storage connection string.
     */
    private String connectionString;

    /**
     * Azure Storage SharedAccessSignature.
     */
    private String sharedAccessSignature;

    /**
     * Azure Storage account name.
     */
    private String accountName;

    /**
     * Get storage account connection string.
     *
     * @return storage account connection string
     */
    public String getConnectionString() {
        return connectionString;
    }

    /**
     * Set storage account connection string.
     *
     * @param connectionString
     */
    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    /**
     * Get storage account sharedAccessSignature.
     *
     * @return storage account sharedAccessSignature
     */
    public String getSharedAccessSignature() {
        return sharedAccessSignature;
    }

    /**
     * Set storage account sharedAccessSignature.
     *
     * @param sharedAccessSignature
     */
    public void setSharedAccessSignature(String sharedAccessSignature) {
        this.sharedAccessSignature = sharedAccessSignature;
    }

    /**
     * Get storage account name.
     *
     * @return
     */
    public String getAccountName() {
        return accountName;
    }

    /**
     * Set storage account name.
     *
     * @param accountName
     */
    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
}
