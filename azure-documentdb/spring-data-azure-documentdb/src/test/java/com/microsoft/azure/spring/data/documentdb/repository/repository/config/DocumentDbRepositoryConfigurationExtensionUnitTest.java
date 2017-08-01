/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.data.documentdb.repository.repository.config;

import com.microsoft.azure.spring.data.documentdb.repository.DocumentDbRepository;
import com.microsoft.azure.spring.data.documentdb.repository.config.DocumentDbRepositoryConfigurationExtension;
import com.microsoft.azure.spring.data.documentdb.repository.config.EnableDocumentDbRepositories;
import org.junit.Test;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource;
import org.springframework.data.repository.config.RepositoryConfiguration;
import org.springframework.data.repository.config.RepositoryConfigurationSource;

import java.util.Collection;

import static org.assertj.core.api.Assertions.fail;

public class DocumentDbRepositoryConfigurationExtensionUnitTest {

    StandardAnnotationMetadata metadata = new StandardAnnotationMetadata(Config.class, true);
    ResourceLoader loader = new PathMatchingResourcePatternResolver();
    Environment environment = new StandardEnvironment();
    RepositoryConfigurationSource configurationSource = new AnnotationRepositoryConfigurationSource(metadata,
            EnableDocumentDbRepositories.class, loader, environment);

    private static void assertHashRepo(Class<?> repositoryInterface,
                                       Collection<RepositoryConfiguration<RepositoryConfigurationSource>> configs) {
        for (final RepositoryConfiguration<?> config : configs) {
            if (config.getRepositoryInterface().equals(repositoryInterface.getName())) {
                return;
            }
        }

        fail("expected to find config for repository interface "
                + repositoryInterface.getName() + ", but got: " + configs.toString());
    }

    @Test
    public void isStrictMatchIfRepositoryExtendsStoreSpecificBase() {
        final DocumentDbRepositoryConfigurationExtension extension = new DocumentDbRepositoryConfigurationExtension();
        assertHashRepo(TestRepository.class, extension.getRepositoryConfigurations(configurationSource, loader, true));
    }

    interface TestRepository extends DocumentDbRepository<Object, String> {
    }

    @EnableDocumentDbRepositories(considerNestedRepositories = true)
    static class Config {

    }
}
