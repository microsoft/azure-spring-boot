/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.data.documentdb.core;

import com.microsoft.azure.documentdb.DocumentCollection;
import com.microsoft.azure.documentdb.RequestOptions;

import java.util.List;

public interface DocumentDbOperations {

    String getCollectionName(Class<?> entityClass);

    DocumentCollection createCollection(String collectionName, RequestOptions collectionOptions);

    <T> List<T> findAll(Class<T> entityClass);

    <T> List<T> findAll(String collectionName, Class<T> entityClass);

    <T> T findById(Object id, Class<T> entityClass);

    <T> T findById(String collectionName, Object id, Class<T> entityClass);

    <T> T insert(T objectToSave);

    <T> T insert(String collectionName, T objectToSave);

    <T> void update(T object, String id);

    <T> void update(String collectionName, T object, String id);

    void deleteById(String collectionName, Object id);

    void deleteAll(String collectionName);
}
