package com.microsoft.azure.spring.data.documentdb.autoconfig;

import com.microsoft.azure.spring.data.documentdb.repository.config.DocumentDbRepositoryConfigurationExtension;
import com.microsoft.azure.spring.data.documentdb.repository.config.EnableDocumentDbRepositories;
import org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

import java.lang.annotation.Annotation;

public class DocumentDbRepositoriesAutoConfigureRegistrar extends AbstractRepositoryConfigurationSourceSupport {
    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return EnableDocumentDbRepositories.class;
    }

    @Override
    protected Class<?> getConfiguration() {
        return EnableDocumentDbRepositoriesConfiguration.class;
    }

    @Override
    protected RepositoryConfigurationExtension getRepositoryConfigurationExtension() {
        return new DocumentDbRepositoryConfigurationExtension();
    }

    @EnableDocumentDbRepositories
    private static class EnableDocumentDbRepositoriesConfiguration {

    }

}
