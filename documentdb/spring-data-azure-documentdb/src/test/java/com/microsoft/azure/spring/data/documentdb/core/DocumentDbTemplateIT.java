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
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScanner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.annotation.Persistent;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.filter;

@RunWith(SpringJUnit4ClassRunner.class)
@PropertySource(value = {"classpath:application.properties"})
public class DocumentDbTemplateIT {

    private static final String TEST_ID = "testid";
    private static final String TEST_NOTEXIST_ID = "testid2";

    private static final String TEST_DB_NAME = "testdb";
    private static final Person TEST_PERSON = new Person(TEST_ID, "testfirstname", "testlastname");

    @Value("${documentdb.uri}")
    private String documentDbUri;
    @Value("${documentdb.key}")
    private String documentDbKey;

    private DocumentClient documentClient;
    private DocumentDbTemplate dbTemplate;

    private MappingDocumentDbConverter dbConverter;
    private DocumentDbMappingContext mappingContext;

    @Autowired
    private ApplicationContext applicationContext;

    @Before
    public void setup() {
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

        dbTemplate.insert(Person.class.getSimpleName(), TEST_PERSON);
    }

    @After
    public void cleanup() {
        dbTemplate.deleteAll(Person.class.getSimpleName());
    }

    @Test(expected = RuntimeException.class)
    public void testInsertDuplicateId() throws Exception {
        dbTemplate.insert(Person.class.getSimpleName(), TEST_PERSON);
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
        final Person result = dbTemplate.findById(Person.class.getSimpleName(), TEST_PERSON.getId(), Person.class);
        assertThat(result.getId()).isEqualTo(TEST_PERSON.getId());
        assertThat(result.getFirstName()).isEqualTo(TEST_PERSON.getFirstName());
        assertThat(result.getLastName()).isEqualTo(TEST_PERSON.getLastName());

        final Person nullResult = dbTemplate.findById(Person.class.getSimpleName(), TEST_NOTEXIST_ID, Person.class);
        assertThat(nullResult).isNull();
    }

    @Test
    public void testUpdate() {
        final Person updated = new Person(TEST_PERSON.getId(), "updatedname",
                TEST_PERSON.getLastName());
        dbTemplate.update(Person.class.getSimpleName(), updated);

        final Person result = dbTemplate.findById(Person.class.getSimpleName(), updated.getId(), Person.class);

        assertThat(result.getId()).isEqualTo(updated.getId());
        assertThat(result.getFirstName()).isEqualTo(updated.getFirstName());
        assertThat(result.getLastName()).isEqualTo(updated.getLastName());
    }

    @Test
    public void testDeleteById() {
        final Person person2 = new Person("newid", "newfn", "newln");
        dbTemplate.insert(person2);
        assertThat(dbTemplate.findAll(Person.class).size()).isEqualTo(2);

        dbTemplate.deleteById(Person.class.getSimpleName(), TEST_PERSON.getId());

        final List<Person> result = dbTemplate.findAll(Person.class);
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getId()).isEqualTo(person2.getId());
        assertThat(result.get(0).getFirstName()).isEqualTo(person2.getFirstName());
        assertThat(result.get(0).getLastName()).isEqualTo(person2.getLastName());

    }
}
