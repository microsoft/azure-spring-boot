/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.java.autoconfigure.cosmosdb;

import com.microsoft.azure.documentdb.ConnectionPolicy;
import com.microsoft.azure.documentdb.ConsistencyLevel;
import com.microsoft.azure.documentdb.DocumentClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnMissingBean(DocumentClient.class)
@EnableConfigurationProperties(AzureCosmosDbProperties.class)
public class AzureCosmosDbAutoConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(AzureCosmosDbAutoConfiguration.class);

    private final AzureCosmosDbProperties properties;

    public AzureCosmosDbAutoConfiguration(AzureCosmosDbProperties properties) {
        this.properties = properties;
    }

    @Bean
    public DocumentClient documentClient() {
        return createDocumentClient();
    }

    private DocumentClient createDocumentClient() {
        LOG.debug("createDocumentClient: hostname = " + properties.getUri() + ", masterkey = " + properties.getKey());

        DocumentClient client = null;
        if (properties.getUri() != null && properties.getKey() != null) {
            client = new DocumentClient(
                    properties.getUri(), properties.getKey(), ConnectionPolicy.GetDefault(), ConsistencyLevel.Session);
        }
        return client;
    }
}
