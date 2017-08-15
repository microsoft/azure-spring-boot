/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.data.documentdb.repository.support;

import com.microsoft.azure.spring.data.documentdb.core.mapping.Document;
import com.microsoft.azure.spring.data.documentdb.domain.Person;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DocumentDbEntityInformationUnitTest {

    @Test
    public void testGetId() {
        final Person testPerson = new Person("test", "test", "test");
        final DocumentDbEntityInformation<Person, String> entityInformation =
                new DocumentDbEntityInformation<Person, String>(Person.class);

        final String idField = entityInformation.getId(testPerson);

        assertThat(idField).isEqualTo(testPerson.getId());
    }

    @Test
    public void testGetIdType() {
        final DocumentDbEntityInformation<Person, String> entityInformation =
                new DocumentDbEntityInformation<Person, String>(Person.class);

        final Class<?> idType = entityInformation.getIdType();
        assertThat(idType.getSimpleName()).isEqualTo(String.class.getSimpleName());
    }

    @Test
    public void testGetCollectionName() {
        final DocumentDbEntityInformation<Person, String> entityInformation =
                new DocumentDbEntityInformation<Person, String>(Person.class);

        final String collectionName = entityInformation.getCollectionName();
        assertThat(collectionName).isEqualTo(Person.class.getSimpleName());
    }

    @Test
    public void testCustomCollectionName() {
        final DocumentDbEntityInformation<Volunteer, String> entityInformation =
                new DocumentDbEntityInformation<Volunteer, String>(Volunteer.class);

        final String collectionName = entityInformation.getCollectionName();
        assertThat(collectionName).isEqualTo("testCollection");
    }

    @Document(collection = "testCollection")
    class Volunteer {
        String id;
        String name;
    }
}
