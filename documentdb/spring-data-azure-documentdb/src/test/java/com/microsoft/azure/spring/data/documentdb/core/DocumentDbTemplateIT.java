/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.data.documentdb.core;

import com.microsoft.azure.documentdb.ConnectionPolicy;
import com.microsoft.azure.documentdb.ConsistencyLevel;
import com.microsoft.azure.documentdb.DocumentClient;
import com.microsoft.azure.spring.data.documentdb.core.convert.MappingDocumentDbConverter;
import com.microsoft.azure.spring.data.documentdb.core.mapping.DocumentDbMappingContext;
import com.microsoft.azure.spring.data.documentdb.domain.Person;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScanner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.annotation.Persistent;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@PropertySource(value = {"classpath:application.properties"})
public class DocumentDbTemplateIT {

    private static final String DOCUMENTDB_URL_PROPERTY_NAME = "documentdb.uri";
    private static final String DOCUMENTDB_KEY_PROPERTY_NAME = "documentdb.key";
    private static final String TEST_DB_NAME = "testdb";
    private static final Person TEST_PERSON = new Person("testid", "testfirstname", "testlastname");
    private String documentDbUri;
    private String documentDbKey;
    private DocumentClient documentClient;
    private DocumentDbTemplate dbTemplate;

    private MappingDocumentDbConverter dbConverter;
    private DocumentDbMappingContext mappingContext;

    @Autowired
    private ApplicationContext applicationContext;

    @Before
    public void setup() {
        documentDbUri = System.getProperty(DOCUMENTDB_URL_PROPERTY_NAME);
        documentDbKey = System.getProperty(DOCUMENTDB_KEY_PROPERTY_NAME);

        mappingContext = new DocumentDbMappingContext();
        try {
            mappingContext.setInitialEntitySet(new EntityScanner(this.applicationContext)
                    .scan(Persistent.class));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e.getMessage());

        }
        dbConverter = new MappingDocumentDbConverter(mappingContext);
        documentClient = new DocumentClient(documentDbUri, documentDbKey,
                ConnectionPolicy.GetDefault(), ConsistencyLevel.Session);

        dbTemplate = new DocumentDbTemplate(documentClient, dbConverter, TEST_DB_NAME);

        dbTemplate.insert(TEST_PERSON);
    }

    @After
    public void cleanup() {
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
                TEST_PERSON.getLastName());
        dbTemplate.update(updated);

        final Person result = dbTemplate.findById(updated.getId(), Person.class);

        assertThat(result.getId()).isEqualTo(updated.getId());
        assertThat(result.getFirstName()).isEqualTo(updated.getFirstName());
        assertThat(result.getLastName()).isEqualTo(updated.getLastName());
    }
}

