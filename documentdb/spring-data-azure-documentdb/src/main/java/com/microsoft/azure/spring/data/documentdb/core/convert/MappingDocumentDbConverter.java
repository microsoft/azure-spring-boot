/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.data.documentdb.core.convert;

import com.microsoft.azure.documentdb.Document;
import com.microsoft.azure.spring.data.documentdb.core.mapping.DocumentDbPersistentEntity;
import com.microsoft.azure.spring.data.documentdb.core.mapping.DocumentDbPersistentProperty;
import org.apache.commons.lang3.ClassUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.data.convert.EntityConverter;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.data.mapping.PropertyHandler;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mapping.model.ConvertingPropertyAccessor;
import org.springframework.data.mapping.model.MappingException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class MappingDocumentDbConverter
        implements EntityConverter<DocumentDbPersistentEntity<?>, DocumentDbPersistentProperty, Object, Document>,
        ApplicationContextAware {

    protected final MappingContext<? extends DocumentDbPersistentEntity<?>,
            DocumentDbPersistentProperty> mappingContext;
    protected GenericConversionService conversionService;
    private ApplicationContext applicationContext;

    public MappingDocumentDbConverter(
            MappingContext<? extends DocumentDbPersistentEntity<?>, DocumentDbPersistentProperty> mappingContext) {
        this.mappingContext = mappingContext;
        this.conversionService = new GenericConversionService();
    }

    @Override
    public <R extends Object> R read(Class<R> type, Document sourceDocument) {
        if (sourceDocument == null) {
            return null;
        }

        final DocumentDbPersistentEntity<?> entity = mappingContext.getPersistentEntity(type);
        return readInternal(entity, type, sourceDocument);
    }

    protected <R extends Object> R readInternal(final DocumentDbPersistentEntity<?> entity, Class<R> type,
                                                final Document sourceDocument) {
        final R result = instantiate(type);

        final PersistentPropertyAccessor accessor = entity.getPropertyAccessor(result);

        final DocumentDbPersistentProperty idProperty = entity.getIdProperty();
        final Object idValue = sourceDocument.getId();

        if (idProperty != null) {
            accessor.setProperty(idProperty, idValue);
        }

        entity.doWithProperties((PropertyHandler<DocumentDbPersistentProperty>) prop -> {
                    if (idProperty != null && idProperty.equals(prop)) {
                        return;
                    }
                    accessor.setProperty(prop, sourceDocument.get(prop.getName()));
                }
        );
        return result;

    }

    @Override
    public void write(Object sourceEntity, Document document) {
        if (sourceEntity == null) {
            return;
        }

        final DocumentDbPersistentEntity<?> entity = mappingContext.getPersistentEntity(sourceEntity.getClass());
        writeInternal(sourceEntity, document, entity);
    }

    public void writeInternal(final Object entity,
                              final Document targetDocument,
                              final DocumentDbPersistentEntity<?> entityInformation) {
        if (entity == null) {
            return;
        }

        if (entityInformation == null) {
            throw new MappingException("no mapping metadata for entity type: " + entity.getClass().getName());
        }

        final ConvertingPropertyAccessor accessor = getPropertyAccessor(entity);
        final DocumentDbPersistentProperty idProperty = entityInformation.getIdProperty();

        if (idProperty != null) {
            targetDocument.setId((String) accessor.getProperty(idProperty));
        }

        for (final Field field : entity.getClass().getDeclaredFields()) {
            if (field.getName().equals(idProperty.getName())) {
                continue;
            }
            targetDocument.set(field.getName(),
                    accessor.getProperty(entityInformation.getPersistentProperty(field.getName())));
        }
    }

    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public ConversionService getConversionService() {
        return conversionService;
    }

    public MappingContext<? extends DocumentDbPersistentEntity<?>, DocumentDbPersistentProperty> getMappingContext() {
        return mappingContext;
    }


    private ConvertingPropertyAccessor getPropertyAccessor(Object entity) {
        final DocumentDbPersistentEntity<?> entityInformation = mappingContext.getPersistentEntity(entity.getClass());
        final PersistentPropertyAccessor accessor = entityInformation.getPropertyAccessor(entity);
        return new ConvertingPropertyAccessor(accessor, conversionService);
    }

    private <T> T instantiate(Class<T> tClass) {
        try {
            final Constructor<T> constructor = (Constructor<T>) tClass.getConstructors()[0];
            final List<Object> params = new ArrayList<Object>();
            for (final Class<?> paramType : constructor.getParameterTypes()) {
                params.add((paramType.isPrimitive()) ? ClassUtils.primitiveToWrapper(paramType).newInstance() : null);
            }

            return constructor.newInstance(params.toArray());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
