/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.data.documentdb.repository.support;


import com.microsoft.azure.spring.data.documentdb.core.DocumentDbOperations;
import com.microsoft.azure.spring.data.documentdb.repository.DocumentDbRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.List;

public class SimpleDocumentDbRepository<T, ID extends Serializable> implements DocumentDbRepository<T, ID> {

    private final DocumentDbOperations documentDbOperations;
    private final DocumentDbEntityInformation<T, ID> entityInformation;

    public SimpleDocumentDbRepository(DocumentDbEntityInformation<T, ID> metadata,
                                      ApplicationContext applicationContext) {
        this.documentDbOperations = applicationContext.getBean(DocumentDbOperations.class);
        this.entityInformation = metadata;
    }

    public SimpleDocumentDbRepository(DocumentDbEntityInformation<T, ID> metadata,
                                      DocumentDbOperations dbOperations) {
        this.documentDbOperations = dbOperations;
        this.entityInformation = metadata;
    }

    @Override
    public <S extends T> S save(S entity) {
        Assert.notNull(entity, "entity must not be null");
        documentDbOperations.insert(entityInformation.getCollectionName(), entity);

        return entity;
    }

    @Override
    public <S extends T> Iterable<S> save(Iterable<S> entities) {
        throw new UnsupportedOperationException("save not supported yet.");
    }

    @Override
    public List<T> findAll() {
        return documentDbOperations.findAll(entityInformation.getCollectionName(), entityInformation.getJavaType());
    }

    @Override
    public List<T> findAll(Iterable<ID> ids) {
        throw new UnsupportedOperationException("findAll not supported yet.");
    }

    @Override
    public T findOne(ID id) {
        Assert.notNull(id, "id must not be null");
        return documentDbOperations.findById(
                entityInformation.getCollectionName(), id, entityInformation.getJavaType());
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        throw new UnsupportedOperationException("findAll(Pageable pageable) not supported yet.");
    }

    @Override
    public List<T> findAll(Sort sort) {
        throw new UnsupportedOperationException("findAll(Sort sort) Sort not supported yet.");
    }

    @Override
    public long count() {
        return findAll().size();
    }

    @Override
    public void delete(ID id) {
        documentDbOperations.deleteById(entityInformation.getCollectionName(), id);
    }

    @Override
    public void delete(T entity) {
        documentDbOperations.deleteById(entityInformation.getCollectionName(), entityInformation.getId(entity));
    }

    @Override
    public void deleteAll() {
        documentDbOperations.deleteAll(entityInformation.getCollectionName());
    }

    @Override
    public void delete(Iterable<? extends T> entities) {
        throw new UnsupportedOperationException("delete not supported yet.");
    }

    @Override
    public boolean exists(ID primaryKey) {
        return findOne(primaryKey) != null;
    }

}
