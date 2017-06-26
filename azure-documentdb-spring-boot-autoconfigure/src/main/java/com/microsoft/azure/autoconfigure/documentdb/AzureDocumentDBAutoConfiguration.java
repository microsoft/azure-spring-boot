/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.autoconfigure.documentdb;

import com.microsoft.azure.documentdb.ConnectionPolicy;
import com.microsoft.azure.documentdb.DocumentClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@ConditionalOnMissingBean(DocumentClient.class)
@EnableConfigurationProperties(AzureDocumentDBProperties.class)
public class AzureDocumentDBAutoConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(AzureDocumentDBAutoConfiguration.class);

    private final AzureDocumentDBProperties properties;
    private final ConnectionPolicy connectionPolicy;

    public AzureDocumentDBAutoConfiguration(AzureDocumentDBProperties properties,
                                            ObjectProvider<ConnectionPolicy> connectionPolicyObjectProvider) {
        this.properties = properties;
        connectionPolicy = connectionPolicyObjectProvider.getIfAvailable();
    }

    @Bean
    @Scope("prototype")
    public DocumentClient documentClient() {
        return createDocumentClient();
    }

    private DocumentClient createDocumentClient() {
        LOG.debug("createDocumentClient");

        DocumentClient client = null;
        if (properties.getUri() != null && properties.getKey() != null) {
            client = new DocumentClient(properties.getUri(), properties.getKey(),
                    connectionPolicy == null ? ConnectionPolicy.GetDefault() : connectionPolicy,
                    properties.getConsistencyLevel());
        }

        if (properties.getUri() == null) {
            LOG.error("Property azure.documentdb.uri is not set.");
        }

        if (properties.getKey() == null) {
            LOG.error("Property azure.documentdb.key is not set.");
        }
        return client;
    }
}
