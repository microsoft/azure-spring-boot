/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.autoconfigure.storage;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("azure.storage")
public class StorageProperties {

    /**
     * Azure Storage connection string.
     */
    @NotEmpty
    private String connectionString;

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
}
