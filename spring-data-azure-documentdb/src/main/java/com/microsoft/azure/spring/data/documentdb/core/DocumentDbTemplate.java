/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.data.documentdb.core;

import com.microsoft.azure.documentdb.*;
import com.microsoft.azure.spring.data.documentdb.DocumentDbFactory;
import com.microsoft.azure.spring.data.documentdb.core.convert.DocumentDbConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import sun.rmi.runtime.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class DocumentDbTemplate implements DocumentDbOperations, ApplicationContextAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentDbTemplate.class);

    private final DocumentDbFactory documentDbFactory;
    private final DocumentDbConverter dbConverter;
    private final String databaseName;

    public DocumentDbTemplate(String host,
                              String key,
                              String dbName,
                              DocumentDbConverter converter) {
        this(new DocumentDbFactory(host, key), converter, dbName);
    }

    public DocumentDbTemplate(DocumentDbFactory documentDbFactory,
                              DocumentDbConverter documentDbConverter,
                              String dbName) {
        Assert.notNull(documentDbFactory, "DocumentDbFactory must not be null!");

        this.documentDbFactory = documentDbFactory;
        this.dbConverter = documentDbConverter;
        this.databaseName = dbName;
    }

    public DocumentDbTemplate(DocumentClient client) {
        this(new DocumentDbFactory(client), new DocumentDbConverter(), null);
    }

    public DocumentDbTemplate(DocumentDbFactory dbFactory) {
        this(dbFactory, new DocumentDbConverter(), null);
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    }

    public String getCollectionName(Class<?> entityClass) {

        return entityClass.getSimpleName();
    }

    public <T> T insert(T objectToSave) {
        // get collection name
        final Class entityClass = objectToSave.getClass();
        final Document document = dbConverter.convertToDocument(objectToSave);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("execute createDocument in database {} collection {}",
                    this.databaseName, getCollectionName(entityClass));
        }

        try {
            // check if collection exists
            final DocumentCollection collection = getCollection(this.databaseName, getCollectionName(entityClass));

            documentDbFactory.getDocumentClient()
                             .createDocument(collection.getSelfLink(), document, null, false);
            return objectToSave;
        } catch (DocumentClientException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public <T> T findById(Object id, Class<T> entityClass) {
        final String query = "SELECT * FROM c WHERE c.id='" + id.toString() + "'";

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("execute queryDocument in database {} collection {} with id {}",
                    this.databaseName, getCollectionName(entityClass), id.toString());
        }

        final List<Document> results = documentDbFactory.getDocumentClient()
                .queryDocuments(getCollectionLink(this.databaseName, getCollectionName(entityClass)), query, null)
                .getQueryIterable().toList();

        if (results == null || results.size() == 0) {
            return null;
        } else {
            final Document d = results.get(0);
            return dbConverter.convertFromDocument(d, entityClass);
        }
    }

    public <T> void delete(T objectToRemove) {
        throw new UnsupportedOperationException("not supported");
    }

    private Database getDb(String dbName) {
        final String query = "SELECT * FROM root r WHERE r.id='" + dbName + "'";

        try {
            final List<Database> dbList = documentDbFactory.getDocumentClient()
                    .queryDatabases(query, null).getQueryIterable().toList();

            if (dbList.size() > 0) {
                return dbList.get(0);
            } else {
                // create new database
                Database db = new Database();
                db.setId(dbName);

                final Resource resource = documentDbFactory.getDocumentClient()
                        .createDatabase(db, null).getResource();

                if (resource instanceof Database) {
                    db = (Database) resource;
                }
                return db;
            }
        } catch (DocumentClientException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public DocumentCollection createCollection(String collectionName, RequestOptions collectionOptions) {
        DocumentCollection collection = new DocumentCollection();
        collection.setId(collectionName);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("execute createCollection in database {} collection {}",
                    this.databaseName, collectionName);
        }

        try {

            final Resource resource = documentDbFactory.getDocumentClient()
                    .createCollection(getDatabaseLink(this.databaseName), collection, collectionOptions)
                    .getResource();
            if (resource instanceof DocumentCollection) {
                collection = (DocumentCollection) resource;
            }
            return collection;
        } catch (DocumentClientException e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    private DocumentCollection getCollection(String dbName, String collectionName) {
        final String query = "SELECT * FROM root r WHERE r.id='" + collectionName + "'";

        try {
            final List<DocumentCollection> collectionList = documentDbFactory.getDocumentClient()
                    .queryCollections(getDb(dbName).getSelfLink(), query, null).getQueryIterable().toList();

            if (collectionList.size() > 0) {
                return collectionList.get(0);
            } else {
                DocumentCollection collection = new DocumentCollection();
                collection.setId(collectionName);

                final RequestOptions requestOptions = new RequestOptions();
                requestOptions.setOfferThroughput(1000);

                final Resource resource = documentDbFactory.getDocumentClient()
                        .createCollection(getDatabaseLink(this.databaseName), collection, requestOptions)
                        .getResource();
                if (resource instanceof DocumentCollection) {
                    collection = (DocumentCollection) resource;
                }
                return collection;
            }
        } catch (DocumentClientException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public void dropCollection(String collectionName) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("execute deleteCollection in database {} collection {}",
                    this.databaseName, collectionName);
        }

        try {
            documentDbFactory.getDocumentClient()
                    .deleteCollection(getCollectionLink(this.databaseName, collectionName), null);
        } catch (DocumentClientException ex) {
            throw new RuntimeException(ex.getMessage());
        }

    }

    public <T> void update(T object) {
        final Field idField = ReflectionUtils.findField(object.getClass(), "id");

        String id;
        try {
            id = idField.get(object).toString();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage());
        }

        final String query = "SELECT * FROM c WHERE c.id='" + id + "'";

        final List<Document> results = documentDbFactory.getDocumentClient()
                .queryDocuments(getCollectionLink(this.databaseName, getCollectionName(object.getClass())),
                        query, null)
                .getQueryIterable().toList();

        if (results == null || results.size() == 0) {
            LOGGER.warn("enitity to update not exists!");
        } else {
            final Document originalDoc = results.get(0);
            final DocumentDbConverter converter = new DocumentDbConverter();

            try {
                documentDbFactory.getDocumentClient().replaceDocument(
                        originalDoc.getSelfLink(),
                        converter.convertToDocument(object), null);
            } catch (DocumentClientException ex) {
                throw new RuntimeException(ex.getMessage());
            }
        }
    }

    public <T> List<T> findAll(Class<T> entityClass) {
        throw new UnsupportedOperationException("not supported");
    }


    public void deleteAll(String collectionName) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("execute deleteCollection in database {} collection {} with id {}",
                    this.databaseName, collectionName);
        }

        try {
            documentDbFactory.getDocumentClient()
                    .deleteCollection(getCollectionLink(this.databaseName, collectionName), null);
        } catch (DocumentClientException ex) {
            LOGGER.warn("deleteAll in database {} collection {} met exception: \n{}",
                    this.databaseName, collectionName, ex.getMessage() );
        }

    }

    public <T> List<T> findAll(String collectionName, final Class<T> entityClass) {
        final List<Document> results = documentDbFactory.getDocumentClient()
                .queryDocuments(getCollectionLink(this.databaseName, collectionName),
                        "SELECT * FROM c", null)
                .getQueryIterable().toList();

        final List<T> entities = new ArrayList<T>();

        for (int i = 0; i < results.size(); i++) {
            final T entity = dbConverter.convertFromDocument(results.get(i), entityClass);
            entities.add(entity);
        }

        return entities;
    }

    public DocumentDbFactory getDocumentDbFactory() {
        return documentDbFactory;
    }

    public DocumentDbConverter getDocumentDbConverter() {
        return this.dbConverter;
    }

    private String getDatabaseLink(String databaseName) {
        return "dbs/" + databaseName;
    }

    private String getCollectionLink(String databaseName, String collectionName) {
        return "dbs/" + databaseName + "/colls/" + collectionName;
    }
}
