/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.data.documentdb.repository;

import com.microsoft.azure.spring.data.documentdb.core.DocumentDbOperations;
import com.microsoft.azure.spring.data.documentdb.repository.domain.Person;
import com.microsoft.azure.spring.data.documentdb.repository.support.DocumentDbEntityInformation;
import com.microsoft.azure.spring.data.documentdb.repository.support.SimpleDocumentDbRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SimpleDocumentDbRepositoryUnitTest {

    SimpleDocumentDbRepository<Person, String> repository;

    @Mock
    DocumentDbOperations dbOperations;
    @Mock
    DocumentDbEntityInformation<Person, String> entityInformation;
    private Person testPerson1;

    @Before
    public void setUp() {
        testPerson1 = new Person("aaa", "firstname", "lastname");

        when(entityInformation.getJavaType()).thenReturn(Person.class);
        when(entityInformation.getCollectionName()).thenReturn(Person.class.getSimpleName());
        when(dbOperations.findAll(Person.class.getSimpleName(), Person.class)).thenReturn(Arrays.asList(testPerson1));

        repository = new SimpleDocumentDbRepository<Person, String>(entityInformation, dbOperations);
    }

    @Test
    public void testSave() {
        repository.save(testPerson1);

        assertEquals(1, repository.findAll().size());
        assertEquals(testPerson1.getFirstName(), repository.findAll().get(0).getFirstName());
    }

}
