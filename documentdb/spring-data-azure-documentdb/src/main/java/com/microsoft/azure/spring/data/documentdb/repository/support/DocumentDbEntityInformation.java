/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.data.documentdb.repository.support;

import com.microsoft.azure.spring.data.documentdb.core.mapping.Document;
import com.microsoft.azure.spring.data.documentdb.core.mapping.PartitionKey;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.core.support.AbstractEntityInformation;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;


public class DocumentDbEntityInformation<T, ID extends Serializable>
        extends AbstractEntityInformation<T, ID> {

    private Field id;
    private Field partitionKeyField;
    private String collectionName;
    private Integer requestUnit;

    public DocumentDbEntityInformation(Class<T> domainClass) {
        super(domainClass);

        this.id = getIdField(domainClass);
        if (this.id != null) {
            ReflectionUtils.makeAccessible(this.id);
        }

        this.collectionName = getCollectionName(domainClass);
        this.partitionKeyField = getPartitionKeyField(domainClass);
        if (this.partitionKeyField != null) {
            ReflectionUtils.makeAccessible(this.partitionKeyField);
        }

        this.requestUnit = getRequestUnit(domainClass);
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

    public Integer getRequestUint() {
        return this.requestUnit;
    }

    public String getPartitionKeyFieldName() {
        return partitionKeyField == null ? null : partitionKeyField.getName();
    }

    public String getPartitionKeyFieldValue(T entity) {
        return partitionKeyField == null ? null : (String) ReflectionUtils.getField(partitionKeyField, entity);
    }

    private Field getIdField(Class<?> domainClass) {
        Field idField = null;

        final List<Field> fields = FieldUtils.getFieldsListWithAnnotation(domainClass, Id.class);

        if (fields.isEmpty()) {
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

    private String getCollectionName(Class<?> domainClass) {
        String customCollectionName = domainClass.getSimpleName();

        final Document annotation = domainClass.getAnnotation(Document.class);

        if (annotation != null && annotation.collection() != null && !annotation.collection().isEmpty()) {
            customCollectionName = annotation.collection();
        }

        return customCollectionName;
    }

    private Field getPartitionKeyField(Class<?> domainClass) {
        Field partitionKeyField = null;

        final List<Field> fields = FieldUtils.getFieldsListWithAnnotation(domainClass, PartitionKey.class);

        if (fields.size() == 1) {
            partitionKeyField = fields.get(0);
        } else if (fields.size() > 1) {
            throw new IllegalArgumentException("Azure Cosmos DB supports only one partition key, " +
                    "only one field with @PartitionKey annotation!");
        }

        if (partitionKeyField != null && partitionKeyField.getType() != String.class) {
            throw new IllegalArgumentException("type of PartitionKey field must be String");
        }
        return partitionKeyField;
    }

    private Integer getRequestUnit(Class<?> domainClass) {
        Integer ru = 4000;
        final Document annotation = domainClass.getAnnotation(Document.class);

        if (annotation != null && annotation.ru() != null && !annotation.ru().isEmpty()) {
            ru = Integer.parseInt(annotation.ru());
        }
        return ru;
    }
}
