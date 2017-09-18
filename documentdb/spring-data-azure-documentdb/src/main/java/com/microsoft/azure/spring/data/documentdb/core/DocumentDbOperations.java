/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.data.documentdb.core;

import com.microsoft.azure.documentdb.DocumentCollection;

import java.util.List;

public interface DocumentDbOperations {

    String getCollectionName(Class<?> entityClass);

    DocumentCollection createCollectionIfNotExists(String collectionName,
                                                   String partitionKeyFieldName,
                                                   Integer requestUnit);

    <T> List<T> findAll(Class<T> entityClass,
                        String partitionKeyFieldName,
                        String partitionKeyFieldValue);

    <T> List<T> findAll(String collectionName,
                        Class<T> entityClass,
                        String partitionKeyFieldName,
                        String partitionKeyFieldValue);

    <T> T findById(Object id,
                   Class<T> entityClass,
                   String partitionKeyFieldValue);

    <T> T findById(String collectionName,
                   Object id,
                   Class<T> entityClass,
                   String partitionKeyFieldValue);

    <T> T insert(T objectToSave, String partitionKeyFieldValue);

    <T> T insert(String collectionName,
                 T objectToSave,
                 String partitionKeyFieldValue);

    <T> void update(T object, String id, String partitionKeyFieldValue);

    <T> void update(String collectionName,
                    T object,
                    String id,
                    String partitionKeyFieldValue);

    <T> void deleteById(String collectionName,
                        Object id,
                        Class<T> domainClass,
                        String partitionKeyFieldValue);

    void deleteAll(String collectionName);
}
