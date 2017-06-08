/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.java.autoconfigure.azurestorage;

import com.microsoft.azure.storage.CloudStorageAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;

/**
 * Created by zhijzhao on 6/8/2017.
 */
@Configuration
@ConditionalOnMissingBean(CloudStorageAccount.class)
@EnableConfigurationProperties(AzureStorageProperties.class)
public class AzureStorageAutoConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(AzureStorageAutoConfiguration.class);

    private final AzureStorageProperties properties;

    public AzureStorageAutoConfiguration(AzureStorageProperties properties) {
        this.properties = properties;
    }

    @Bean
    public CloudStorageAccount cloudStorageAccount() {
        LOG.debug("cloudStorageAccount called, account name = " + properties.getName());
        return createCloudStorageAccount();
    }

    private CloudStorageAccount createCloudStorageAccount() {
        LOG.debug("createCloudStorageAccount called, account name = " + properties.getName());

        CloudStorageAccount account = null;
        if (properties.getName() != null) {
            try {
                String connectionString = properties.buildStorageConnectString();
                LOG.debug("Connection String is " + connectionString);
                account = CloudStorageAccount.parse(connectionString);
                LOG.debug("createCloudStorageAccount created account " + account);
            } catch (InvalidKeyException e) {
                String msg = "Error creating CloudStorageAccount: " + e.getMessage();
                LOG.error(msg, e);
            } catch (URISyntaxException e) {
                String msg = "Error creating CloudStorageAccount: " + e.getMessage();
                LOG.error(msg, e);
            }
        }
        return account;
    }
}
