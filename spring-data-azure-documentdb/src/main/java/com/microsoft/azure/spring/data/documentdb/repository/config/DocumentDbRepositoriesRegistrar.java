package com.microsoft.azure.spring.data.documentdb.repository.config;

import org.springframework.data.repository.config.RepositoryBeanDefinitionRegistrarSupport;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

import java.lang.annotation.Annotation;


public class DocumentDbRepositoriesRegistrar extends RepositoryBeanDefinitionRegistrarSupport {

    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return EnableDocumentDbRepositories.class;
    }

    @Override
    protected RepositoryConfigurationExtension getExtension() {
        return new DocumentDbRepositoryConfigurationExtension();
    }


}
