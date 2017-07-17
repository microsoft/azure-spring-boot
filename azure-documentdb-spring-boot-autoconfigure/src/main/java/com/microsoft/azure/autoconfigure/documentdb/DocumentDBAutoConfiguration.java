/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.autoconfigure.documentdb;

import com.microsoft.azure.documentdb.ConnectionPolicy;
import com.microsoft.azure.documentdb.ConsistencyLevel;
import com.microsoft.azure.documentdb.DocumentClient;
import com.microsoft.azure.spring.data.documentdb.DocumentDbFactory;
import com.microsoft.azure.spring.data.documentdb.core.DocumentDbTemplate;
import com.microsoft.azure.spring.data.documentdb.core.convert.DocumentDbConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@ConditionalOnClass(DocumentClient.class)
@ConditionalOnMissingBean(type =
        {"com.microsoft.azure.spring.data.documentdb.DocumentDbFactory",
                "com.microsoft.azure.documentdb.DocumentClient"})
@EnableConfigurationProperties(DocumentDBProperties.class)
public class DocumentDBAutoConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(DocumentDBAutoConfiguration.class);

    private final DocumentDBProperties properties;
    private final ConnectionPolicy connectionPolicy;

    public DocumentDBAutoConfiguration(DocumentDBProperties properties,
                                       ObjectProvider<ConnectionPolicy> connectionPolicyObjectProvider) {
        this.properties = properties;
        connectionPolicy = connectionPolicyObjectProvider.getIfAvailable();
    }

    @Bean
    @Scope("prototype")
    @ConditionalOnMissingBean
    public DocumentClient documentClient() {
        return createDocumentClient();
    }

    private DocumentClient createDocumentClient() {
        LOG.debug("createDocumentClient");
        return new DocumentClient(properties.getUri(), properties.getKey(),
                connectionPolicy == null ? ConnectionPolicy.GetDefault() : connectionPolicy,
                properties.getConsistencyLevel() == null ?
                        ConsistencyLevel.Session : properties.getConsistencyLevel());
    }

    @Bean
    @ConditionalOnMissingBean
    public DocumentDbFactory documentDbFactory() {
        return new DocumentDbFactory(this.documentClient());
    }

    @Bean
    @ConditionalOnMissingBean
    public DocumentDbTemplate documentDbTemplate() {
        return new DocumentDbTemplate(this.documentDbFactory(),
                new DocumentDbConverter(),
                properties.getDatabase());
    }
}
