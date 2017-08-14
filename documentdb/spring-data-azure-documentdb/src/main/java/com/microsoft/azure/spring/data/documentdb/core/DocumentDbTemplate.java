/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.data.documentdb.core;

import com.microsoft.azure.documentdb.*;
import com.microsoft.azure.spring.data.documentdb.DocumentDbFactory;
import com.microsoft.azure.spring.data.documentdb.core.convert.MappingDocumentDbConverter;
import com.microsoft.azure.spring.data.documentdb.core.mapping.DocumentDbPersistentEntity;
import com.microsoft.azure.spring.data.documentdb.core.mapping.DocumentDbPersistentProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class DocumentDbTemplate implements DocumentDbOperations, ApplicationContextAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentDbTemplate.class);

    private final DocumentDbFactory documentDbFactory;
    private final MappingDocumentDbConverter mappingDocumentDbConverter;
    private final String databaseName;
    private final MappingContext<? extends DocumentDbPersistentEntity<?>, DocumentDbPersistentProperty> mappingContext;

    private Database databaseCache;
    private List<String> collectionCache;

    public DocumentDbTemplate(DocumentDbFactory documentDbFactory,
                              MappingDocumentDbConverter mappingDocumentDbConverter,
                              String dbName) {
        Assert.notNull(documentDbFactory, "DocumentDbFactory must not be null!");
        Assert.notNull(mappingDocumentDbConverter, "MappingDocumentDbConverter must not be null!");

        this.databaseName = dbName;
        this.documentDbFactory = documentDbFactory;
        this.mappingDocumentDbConverter = mappingDocumentDbConverter;
        this.mappingContext = mappingDocumentDbConverter.getMappingContext();
        this.collectionCache = new ArrayList<String>();
    }

    public DocumentDbTemplate(DocumentClient client,
                              MappingDocumentDbConverter mappingDocumentDbConverter,
                              String dbName) {

        this(new DocumentDbFactory(client), mappingDocumentDbConverter, dbName);
    }


    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    }

    public <T> T insert(T objectToSave) {
        final Class<? extends Object> entityClass = objectToSave.getClass();
        final Document document = new Document();
        mappingDocumentDbConverter.write(objectToSave, document);
        final String collectionName = getCollectionName(entityClass);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("execute createDocument in database {} collection {}",
                    this.databaseName, collectionName);
        }

        try {
            if (!this.collectionCache.contains(collectionName)) {
                createOrGetCollection(this.databaseName, collectionName);
                this.collectionCache.add(collectionName);
            }

            documentDbFactory.getDocumentClient()
                    .createDocument(getCollectionLink(this.databaseName, collectionName), document, null, false);
            return objectToSave;
        } catch (DocumentClientException e) {
            throw new RuntimeException("insert exception", e);
        }
    }

    public <T> T findById(Object id, Class<T> entityClass) {
        final String collectionName = getCollectionName(entityClass);

        try {
            final Resource resource = documentDbFactory.getDocumentClient()
                    .readDocument(
                            getDocumentLink(this.databaseName, collectionName, id.toString()), null)
                    .getResource();

            if (resource instanceof Document) {
                final Document document = (Document) resource;
                return mappingDocumentDbConverter.read(entityClass, document);
            } else {
                return null;
            }
        } catch (DocumentClientException e) {
            throw new RuntimeException("findById exception", e);
        }
    }

    public <T> void delete(T objectToRemove) {
        throw new UnsupportedOperationException("not supported");
    }


    public <T> void update(T object) {
        final Field idField = ReflectionUtils.findField(object.getClass(), "id");

        final String id;
        try {
            ReflectionUtils.makeAccessible(idField);
            id = idField.get(object).toString();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage());
        }

        try {
            final Resource resource = documentDbFactory.getDocumentClient()
                    .readDocument(getDocumentLink(this.databaseName, getCollectionName(object.getClass()), id), null)
                    .getResource();

            if (resource instanceof Document) {
                final Document originalDoc = (Document) resource;

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("execute replaceDocument in database {} collection {} with id {}",
                            this.databaseName, getCollectionName(object.getClass()), id);
                }

                mappingDocumentDbConverter.write(object, originalDoc);

                documentDbFactory.getDocumentClient().replaceDocument(
                        originalDoc.getSelfLink(),
                        originalDoc,
                        null);
            } else {
                LOGGER.error("invalid Document to update {}", resource.getSelfLink());
                throw new RuntimeException("invalid Document to update " + resource.getSelfLink());
            }
        } catch (DocumentClientException ex) {
            throw new RuntimeException("update exception", ex);
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
            if (ex.getStatusCode() == 404) {
                LOGGER.warn("deleteAll in database {} collection {} met NOTFOUND error {}",
                        this.databaseName, collectionName, ex.getMessage());
            } else {
                throw new RuntimeException("deleteAll exception", ex);
            }
        }

    }

    public <T> List<T> findAll(String collectionName, final Class<T> entityClass) {
        final List<Document> results = documentDbFactory.getDocumentClient()
                .queryDocuments(getCollectionLink(this.databaseName, collectionName),
                        "SELECT * FROM c", null)
                .getQueryIterable().toList();

        final List<T> entities = new ArrayList<T>();

        for (int i = 0; i < results.size(); i++) {
            final T entity = mappingDocumentDbConverter.read(entityClass, results.get(i));
            entities.add(entity);
        }

        return entities;
    }

    public String getCollectionName(Class<?> entityClass) {
        return entityClass.getSimpleName();
    }

    private Database createOrGetDatabase(String dbName) {
        try {
            final List<Database> dbList = documentDbFactory.getDocumentClient()
                    .queryDatabases(new SqlQuerySpec("SELECT * FROM root r WHERE r.id=@id",
                            new SqlParameterCollection(new SqlParameter("@id", dbName))), null)
                    .getQueryIterable().toList();

            if (dbList.size() > 0) {
                return dbList.get(0);
            } else {
                // create new database
                final Database db = new Database();
                db.setId(dbName);

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("execute createDatabase {}", dbName);
                }

                final Resource resource = documentDbFactory.getDocumentClient()
                        .createDatabase(db, null).getResource();

                if (resource instanceof Database) {
                    return (Database) resource;
                } else {
                    LOGGER.error("create database {} get unexpected result: {}" + resource.getSelfLink());
                    throw new RuntimeException("create database {} get unexpected result: " + resource.getSelfLink());
                }
            }
        } catch (DocumentClientException ex) {
            throw new RuntimeException("createOrGetDatabase exception", ex);
        }
    }

    public DocumentCollection createCollection(String collectionName, RequestOptions collectionOptions) {
        return createCollection(this.databaseName, collectionName, collectionOptions);
    }

    public DocumentCollection createCollection(String dbName,
                                               String collectionName,
                                               RequestOptions collectionOptions) {
        DocumentCollection collection = new DocumentCollection();
        collection.setId(collectionName);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("execute createCollection in database {} collection {}", dbName, collectionName);
        }

        try {
            final Resource resource = documentDbFactory.getDocumentClient()
                    .createCollection(getDatabaseLink(dbName), collection, collectionOptions)
                    .getResource();
            if (resource instanceof DocumentCollection) {
                collection = (DocumentCollection) resource;
            }
            return collection;
        } catch (DocumentClientException e) {
            throw new RuntimeException("createCollection exception", e);
        }

    }

    private DocumentCollection createOrGetCollection(String dbName, String collectionName) {
        if (this.databaseCache == null) {
            this.databaseCache = createOrGetDatabase(dbName);
        }

        final List<DocumentCollection> collectionList = documentDbFactory.getDocumentClient()
                .queryCollections(getDatabaseLink(dbName),
                        new SqlQuerySpec("SELECT * FROM root r WHERE r.id=@id",
                                new SqlParameterCollection(new SqlParameter("@id", collectionName))), null)
                .getQueryIterable().toList();

        if (collectionList.size() > 0) {
            return collectionList.get(0);
        } else {
            final RequestOptions requestOptions = new RequestOptions();
            requestOptions.setOfferThroughput(1000);

            return createCollection(dbName, collectionName, requestOptions);
        }
    }

    public void dropCollection(String collectionName) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("execute deleteCollection in database {} collection {}", this.databaseName, collectionName);
        }

        try {
            documentDbFactory.getDocumentClient()
                    .deleteCollection(getCollectionLink(this.databaseName, collectionName), null);
        } catch (DocumentClientException ex) {
            throw new RuntimeException("dropCollection exception", ex);
        }

    }

    private String getDatabaseLink(String databaseName) {
        return "dbs/" + databaseName;
    }

    private String getCollectionLink(String databaseName, String collectionName) {
        return getDatabaseLink(databaseName) + "/colls/" + collectionName;
    }

    private String getDocumentLink(String databaseName, String collectionName, String documentId) {
        return getCollectionLink(databaseName, collectionName) + "/docs/" + documentId;
    }
}
