/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.data.documentdb.repository.support;

import com.microsoft.azure.spring.data.documentdb.core.DocumentDbOperations;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import java.io.Serializable;


public class DocumentDbRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable>
        extends RepositoryFactoryBeanSupport<T, S, ID>
        implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    private DocumentDbOperations operations;

    public DocumentDbRepositoryFactoryBean(Class<? extends T> repositoryInterface) {

        super(repositoryInterface);
    }

    public void setDocumentDbOperations(DocumentDbOperations operations) {
        this.operations = operations;		
    }
 
    @Override
    protected final RepositoryFactorySupport createRepositoryFactory() {
        return getFactoryInstance(applicationContext);
    }

    protected RepositoryFactorySupport getFactoryInstance(ApplicationContext applicationContext) {
        return new DocumentDbRepositoryFactory(applicationContext);
    }

    @Override
    protected void setMappingContext(MappingContext<?, ?> mappingContext) {
        super.setMappingContext(mappingContext);
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
