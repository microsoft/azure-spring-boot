/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.data.documentdb.repository.support;

import com.microsoft.azure.spring.data.documentdb.core.mapping.DocumentDbPersistentEntity;
import com.microsoft.azure.spring.data.documentdb.domain.Person;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.repository.core.EntityInformation;

import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class DocumentDbRepositoryFactoryUnitTest {

    @Mock
    MappingContext mappingContext;

    @Mock
    DocumentDbPersistentEntity entity;

    @Autowired
    ApplicationContext applicationContext;

    @Test
    public void useMappingDocumentDBEntityInfoIfMappingContextSet() {
        final DocumentDbRepositoryFactory factory = new DocumentDbRepositoryFactory(applicationContext);
        final EntityInformation<Person, String> entityInfo = factory.getEntityInformation(Person.class);
        assertTrue(entityInfo instanceof DocumentDbEntityInformation);
    }
}
