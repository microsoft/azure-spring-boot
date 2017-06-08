/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.java.autoconfigure.azurestorage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by zhijzhao on 6/8/2017.
 */
@ConfigurationProperties("azure.storage.account")
public class AzureStorageProperties {
    private static final Logger LOG = LoggerFactory.getLogger(AzureStorageProperties.class);

    private String name;
    private String key;

    /**
     * Get storage account name.
     *
     * @return storage account name
     */
    public String getName() {
        return name;
    }

    /**
     * Set storage account name.
     *
     * @param accountName
     */
    public void setName(String accountName) {
        this.name = accountName;
    }

    /**
     * Get azure storage account key.
     *
     * @return azure storage account key
     */
    public String getKey() {
        return key;
    }

    /**
     * Set azure storage account key.
     *
     * @param accountKey
     */
    public void setKey(String accountKey) {
        this.key = accountKey;
    }

    /**
     * Construct azure storage connnection string.
     *
     * @return azure storage connection string
     */
    public String buildStorageConnectString() {
         String storageConnectionString =
                "DefaultEndpointsProtocol=http;" + "AccountName=" + getName() + ";" + "AccountKey=" + getKey();
        LOG.debug("storageConnectionString = " + storageConnectionString);
        return storageConnectionString;
    }
}
