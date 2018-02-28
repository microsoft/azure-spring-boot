/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.autoconfigure.storage;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageCredentialsSharedAccessSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;

@Configuration
@ConditionalOnMissingBean(CloudStorageAccount.class)
@EnableConfigurationProperties(StorageProperties.class)
@ConditionalOnExpression("'${azure.storage.connection-string}' != null " +
        "|| '${azure.storage.shared-access-signature}' != null")
public class StorageAutoConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(StorageAutoConfiguration.class);

    private final StorageProperties properties;

    public StorageAutoConfiguration(StorageProperties properties) {
        this.properties = properties;
    }

    /**
     * Declare CloudStorageAccount bean.
     *
     * @return CloudStorageAccount bean
     */
    @Bean
    @Scope("prototype")
    @ConditionalOnProperty(prefix = "azure.storage", value = "connection-string")
    public CloudStorageAccount cloudStorageAccount() throws URISyntaxException, InvalidKeyException {
        LOG.debug("cloudStorageAccount with connection-string called");
        return CloudStorageAccount.parse(properties.getConnectionString());
    }

    @Bean
    @Scope("prototype")
    @ConditionalOnProperty(prefix = "azure.storage", value = {"shared-access-signature", "account-name"})
    public CloudStorageAccount cloudStorageAccountSas() throws URISyntaxException, InvalidKeyException {
        LOG.debug("createCloudStorageAccount with sas called");
        final StorageCredentialsSharedAccessSignature sasToken =
                new StorageCredentialsSharedAccessSignature(properties.getSharedAccessSignature());
        return new CloudStorageAccount(sasToken, true, null, properties.getAccountName());
    }

}
