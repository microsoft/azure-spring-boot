/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.autoconfigure.azurestorage;

import com.microsoft.azure.storage.CloudStorageAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;

@Configuration
@ConditionalOnMissingBean(CloudStorageAccount.class)
@EnableConfigurationProperties(AzureStorageProperties.class)
public class AzureStorageAutoConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(AzureStorageAutoConfiguration.class);

    private final AzureStorageProperties properties;

    public AzureStorageAutoConfiguration(AzureStorageProperties properties) {
        this.properties = properties;
    }

    /**
     * Declare CloudStorageAccount bean.
     *
     * @return CloudStorageAccount bean
     */
    @Bean
    @Scope("prototype")
    public CloudStorageAccount cloudStorageAccount() {
        LOG.debug("cloudStorageAccount called, connection string = " + properties.getConnectionString());
        return createCloudStorageAccount();
    }

    /**
     * Helper function for creating CloudStorageAccount instance from storage connection string.
     *
     * @return CloudStorageAccount object
     */
    private CloudStorageAccount createCloudStorageAccount() {
        LOG.debug("createCloudStorageAccount called");

        CloudStorageAccount account = null;
        if (properties.getConnectionString() != null) {
            try {
                final String connectionString = properties.getConnectionString();
                LOG.debug("Connection string is " + connectionString);
                account = CloudStorageAccount.parse(connectionString);
                LOG.debug("createCloudStorageAccount created account " + account);
            } catch (InvalidKeyException e) {
                final String msg = "Error creating CloudStorageAccount: " + e.getMessage();
                LOG.error(msg, e);
                throw new AzureStorageAutoConfigureException(msg, e);
            } catch (URISyntaxException e) {
                final String msg = "Error creating CloudStorageAccount: " + e.getMessage();
                LOG.error(msg, e);
                throw new AzureStorageAutoConfigureException(msg, e);
            }
        }
        return account;
    }
}
