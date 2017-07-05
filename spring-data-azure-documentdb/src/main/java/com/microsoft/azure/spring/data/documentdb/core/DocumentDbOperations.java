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

    <T> DocumentCollection createCollection(String collectionName, RequestOptions collectionOptions);

    void dropCollection(String collectionName);

    <T> List<T> findAll(Class<T> entityClass);

    <T> List<T> findAll(String collectionName, Class<T> entityClass);

    <T> T findById(Object id, Class<T> entityClass);

    <T> T insert(T objectToSave);

    <T> void update(T object);

    <T> void delete(T objectToRemove);

    void deleteAll(String collectionName);
}
