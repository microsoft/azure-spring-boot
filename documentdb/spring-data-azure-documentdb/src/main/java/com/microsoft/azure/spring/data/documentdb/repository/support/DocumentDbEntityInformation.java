/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.data.documentdb.repository.support;

import com.microsoft.azure.spring.data.documentdb.core.mapping.Document;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.core.support.AbstractEntityInformation;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;


public class DocumentDbEntityInformation<T, ID extends Serializable>
        extends AbstractEntityInformation<T, ID> {

    private Field id;
    private String collectionName;

    public DocumentDbEntityInformation(Class<T> domainClass) {
        super(domainClass);

        this.id = getIdField(domainClass);
        if (this.id != null) {
            ReflectionUtils.makeAccessible(this.id);
        }

        this.collectionName = getCustomCollection(domainClass);
    }


    public ID getId(T entity) {
        return (ID) ReflectionUtils.getField(id, entity);
    }

    public Class<ID> getIdType() {
        return (Class<ID>) id.getType();
    }

    public String getCollectionName() {
        return this.collectionName;
    }

    private Field getIdField(Class<?> domainClass) {
        Field idField = null;

        final List<Field> fields = FieldUtils.getFieldsListWithAnnotation(domainClass, Id.class);

        if (fields.size() == 0) {
            idField = ReflectionUtils.findField(getJavaType(), "id");
        } else if (fields.size() == 1) {
            idField = fields.get(0);
        } else {
            throw new IllegalArgumentException("only one field with @Id annotation!");
        }

        if (idField != null && idField.getType() != String.class) {
            throw new IllegalArgumentException("type of id field must be String");
        }
        return idField;
    }

    private String getCustomCollection(Class<?> domainClass) {
        String collectionName = domainClass.getSimpleName();

        final Document annotation = domainClass.getAnnotation(Document.class);

        if (annotation != null && annotation.collection() != null && !annotation.collection().isEmpty()) {
            collectionName = annotation.collection();
        }

        return collectionName;
    }
}
