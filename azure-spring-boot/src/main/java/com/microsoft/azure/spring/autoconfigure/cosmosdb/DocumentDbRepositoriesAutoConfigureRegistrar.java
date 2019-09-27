/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.autoconfigure.cosmosdb;

import com.microsoft.azure.spring.data.cosmosdb.repository.config.CosmosRepositoryConfigurationExtension;
import com.microsoft.azure.spring.data.cosmosdb.repository.config.EnableCosmosRepositories;
import com.microsoft.azure.spring.data.cosmosdb.repository.config.EnableReactiveCosmosRepositories;
import com.microsoft.azure.spring.data.cosmosdb.repository.config.ReactiveCosmosRepositoryConfigurationExtension;
import org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

import java.lang.annotation.Annotation;

public class DocumentDbRepositoriesAutoConfigureRegistrar extends AbstractRepositoryConfigurationSourceSupport {
    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return EnableReactiveCosmosRepositories.class;
    }

    @Override
    protected Class<?> getConfiguration() {
        return EnableDocumentDbRepositoriesConfiguration.class;
    }

    @Override
    protected RepositoryConfigurationExtension getRepositoryConfigurationExtension() {
        return new ReactiveCosmosRepositoryConfigurationExtension();
    }

    @EnableReactiveCosmosRepositories
    private static class EnableDocumentDbRepositoriesConfiguration {

    }

}
