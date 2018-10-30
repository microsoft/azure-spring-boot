/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.autoconfigure.cosmosdb;

import com.microsoft.azure.documentdb.ConnectionPolicy;
import com.microsoft.azure.documentdb.DocumentClient;
import com.microsoft.azure.spring.data.cosmosdb.config.AbstractDocumentDbConfiguration;
import com.microsoft.azure.spring.data.cosmosdb.config.DocumentDBConfig;
import com.microsoft.azure.spring.data.cosmosdb.core.DocumentDbTemplate;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({DocumentClient.class, DocumentDbTemplate.class})
@EnableConfigurationProperties(DocumentDBProperties.class)
public class DocumentDBAutoConfiguration extends AbstractDocumentDbConfiguration {
    private final DocumentDBProperties properties;
    private final ConnectionPolicy policy;

    public DocumentDBAutoConfiguration(DocumentDBProperties properties,
                                       ObjectProvider<ConnectionPolicy> connectionPolicyObjectProvider) {
        this.properties = properties;
        this.policy = connectionPolicyObjectProvider.getIfAvailable();
        configConnectionPolicy(properties, policy);
    }

    @Override
    public DocumentDBConfig getConfig() {
        final DocumentDBConfig config = DocumentDBConfig.builder(
                properties.getUri(), properties.getKey(), properties.getDatabase())
                .consistencyLevel(properties.getConsistencyLevel())
                .allowTelemetry(properties.isAllowTelemetry())
                .connectionPolicy(properties.getConnectionPolicy())
                .build();

        return config;
    }

    private void configConnectionPolicy(DocumentDBProperties properties, ConnectionPolicy connectionPolicy) {
        // This is a temp fix as DocumentDbFactory does not support loading ConnectionPolicy bean from context
        final ConnectionPolicy policy = connectionPolicy == null ? ConnectionPolicy.GetDefault() : connectionPolicy;

        properties.setConnectionPolicy(policy);
    }
}
