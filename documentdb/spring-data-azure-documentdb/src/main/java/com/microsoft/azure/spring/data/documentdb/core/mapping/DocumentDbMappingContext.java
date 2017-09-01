/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.data.documentdb.core.mapping;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.mapping.context.AbstractMappingContext;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.data.util.TypeInformation;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;


public class DocumentDbMappingContext
        extends AbstractMappingContext<BasicDocumentDbPersistentEntity<?>, DocumentDbPersistentProperty>
        implements ApplicationContextAware {

    private ApplicationContext context;

    @Override
    protected <T> BasicDocumentDbPersistentEntity<T> createPersistentEntity(TypeInformation<T> typeInformation) {
        final BasicDocumentDbPersistentEntity<T> entity = new BasicDocumentDbPersistentEntity<>(typeInformation);

        if (context != null) {
            entity.setApplicationContext(context);
        }
        return entity;
    }

    @Override
    public DocumentDbPersistentProperty createPersistentProperty(Field field, PropertyDescriptor propertyDescriptor,
                                                                 BasicDocumentDbPersistentEntity<?> owner,
                                                                 SimpleTypeHolder simpleTypeHolder) {
        return new BasicDocumentDbPersistentProperty(field, propertyDescriptor, owner,
                simpleTypeHolder);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.context = applicationContext;
    }
}
