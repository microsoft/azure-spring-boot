package com.microsoft.azure.spring.data.documentdb.core.mapping;

import org.springframework.data.mapping.PersistentEntity;


public interface DocumentDbPersistentEntity<T> extends PersistentEntity<T, DocumentDbPersistentProperty> {

    String getCollection();

    String getLanguage();
}
