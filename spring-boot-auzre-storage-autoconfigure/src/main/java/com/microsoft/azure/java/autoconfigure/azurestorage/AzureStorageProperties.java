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

    public String getName()
    {
        return name;
    }

    public void setName(String accountName)
    {
        this.name = accountName;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String accountKey)
    {
        this.key = accountKey;
    }

    public String buildStorageConnectString()
    {
        String storageConnectionString = "DefaultEndpointsProtocol=http;" + "AccountName=" + getName() + ";" + "AccountKey=" + getKey();
        LOG.debug("storageConnectionString = " + storageConnectionString);
        return storageConnectionString;
    }

    @Override
    public String toString()
    {
        return "AzureStorageProperties [name=" + name + ", key=" + key + "]";
    }
}
