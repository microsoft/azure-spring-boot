/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.data.documentdb.repository.core;

import com.microsoft.azure.documentdb.ConnectionPolicy;
import com.microsoft.azure.documentdb.ConsistencyLevel;
import com.microsoft.azure.documentdb.DocumentClient;
import com.microsoft.azure.spring.data.documentdb.core.DocumentDbTemplate;
import com.microsoft.azure.spring.data.documentdb.repository.domain.Person;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@PropertySource(value = {"classpath:application.properties"})
public class DocumentDbTemplateIT {

    private static String documentDbUri;
    private static String documentDbKey;

    private static DocumentClient documentClient;
    private static DocumentDbTemplate dbTemplate;

    private static final String DOCUMENTDB_URL_PROPERTY_NAME = "documentdb.uri";
    private static final String DOCUMENTDB_KEY_PROPERTY_NAME = "documentdb.key";
    private static final String TEST_DB_NAME = "testdb";
    private static final Person TEST_PERSON = new Person("testid", "testfirstname", "testlastname", "111");
    
    @BeforeClass
    public static void setup() {
        documentDbUri = System.getProperty(DOCUMENTDB_URL_PROPERTY_NAME);
        documentDbKey = System.getProperty(DOCUMENTDB_KEY_PROPERTY_NAME);

        documentClient = new DocumentClient(documentDbUri, documentDbKey,
                ConnectionPolicy.GetDefault(), ConsistencyLevel.Session);
        dbTemplate = new DocumentDbTemplate(documentClient, TEST_DB_NAME);

        dbTemplate.insert(TEST_PERSON);
    }

    @AfterClass
    public static void cleanup() {
        dbTemplate.deleteAll(Person.class.getSimpleName());
    }

    @Test(expected = RuntimeException.class)
    public void testInsertDuplicateId() throws Exception {
        dbTemplate.insert(TEST_PERSON);
    }

    @Test
    public void testFindAll() {
        final List<Person> result = dbTemplate.findAll(Person.class.getSimpleName(), Person.class);
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getId()).isEqualTo(TEST_PERSON.getId());
        assertThat(result.get(0).getFirstName()).isEqualTo(TEST_PERSON.getFirstName());
        assertThat(result.get(0).getLastName()).isEqualTo(TEST_PERSON.getLastName());
    }

    @Test
    public void testFindById() {
        final Person result = dbTemplate.findById(TEST_PERSON.getId(), Person.class);
        assertThat(result.getId()).isEqualTo(TEST_PERSON.getId());
        assertThat(result.getFirstName()).isEqualTo(TEST_PERSON.getFirstName());
        assertThat(result.getLastName()).isEqualTo(TEST_PERSON.getLastName());
    }

    @Test
    public void testUpdate() {
        final Person updated = new Person(TEST_PERSON.getId(), "updatedname",
                TEST_PERSON.getLastName(), TEST_PERSON.getPhone());
        dbTemplate.update(updated);

        final Person result = dbTemplate.findById(updated.getId(), Person.class);

        assertThat(result.getId()).isEqualTo(updated.getId());
        assertThat(result.getFirstName()).isEqualTo(updated.getFirstName());
        assertThat(result.getLastName()).isEqualTo(updated.getLastName());
    }
}

