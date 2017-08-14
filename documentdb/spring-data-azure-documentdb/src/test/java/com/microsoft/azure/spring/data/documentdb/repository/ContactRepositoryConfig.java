/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.data.documentdb.repository;

import com.microsoft.azure.documentdb.ConnectionPolicy;
import com.microsoft.azure.documentdb.ConsistencyLevel;
import com.microsoft.azure.documentdb.DocumentClient;
import com.microsoft.azure.spring.data.documentdb.config.AbstractDocumentDbConfiguration;
import com.microsoft.azure.spring.data.documentdb.config.EnableDocumentDbRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableDocumentDbRepositories
@PropertySource(value = {"classpath:application.properties"})
public class ContactRepositoryConfig extends AbstractDocumentDbConfiguration {
    @Value("${documentdb.uri}")
    String dbUri;

    @Value("${documentdb.key}")
    String dbKey;

    @Override
    @Bean
    public DocumentClient documentClient() {
        return new DocumentClient(dbUri, dbKey, ConnectionPolicy.GetDefault(), ConsistencyLevel.Session);
    }

    @Override
    public String getDatabase() {
        return "contact";
    }
}
