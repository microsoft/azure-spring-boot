/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.data.documentdb.repository;

import com.microsoft.azure.documentdb.ConnectionPolicy;
import com.microsoft.azure.documentdb.ConsistencyLevel;
import com.microsoft.azure.documentdb.DocumentClient;
import com.microsoft.azure.spring.data.documentdb.config.AbstractDocumentDbConfiguration;
import com.microsoft.azure.spring.data.documentdb.config.EnableDocumentDbRepositories;
import com.microsoft.azure.spring.data.documentdb.domain.Person;
import com.microsoft.azure.spring.data.documentdb.domain.PersonRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class PersonRepositoryIT {

    @Autowired
    PersonRepository repository;

    private static final Person TEST_PERSON = new Person("testId", "testFn", "testLn");

    @Before
    public void setup() {

    }

    @After
    public void cleanup() {
        repository.deleteAll();
    }

    @Test
    public void testSave() {
        repository.save(TEST_PERSON);

        final List<Person> result = repository.findAll();

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getId()).isEqualTo(TEST_PERSON.getId());
        assertThat(result.get(0).getFirstName()).isEqualTo(TEST_PERSON.getFirstName());
        assertThat(result.get(0).getLastName()).isEqualTo(TEST_PERSON.getLastName());
    }

    @Test
    public void testFindbyId() {
        repository.save(TEST_PERSON);

        final Person result = repository.findOne(TEST_PERSON.getId());

        assertThat(result.getId()).isEqualTo(TEST_PERSON.getId());
        assertThat(result.getFirstName()).isEqualTo(TEST_PERSON.getFirstName());
        assertThat(result.getLastName()).isEqualTo(TEST_PERSON.getLastName());
    }

    @Test
    public void testDeleteAll() {
        repository.deleteAll();

        final List<Person> result = repository.findAll();

        assertThat(result.size()).isEqualTo(0);
    }

    @Configuration
    @EnableDocumentDbRepositories(basePackages = "com.microsoft.azure.spring.data.documentdb")
    @PropertySource(value = {"classpath:application.properties"})
    class RepositoryConfig extends AbstractDocumentDbConfiguration {
        @Value("documentdb.uri")
        String dbUri;

        @Value("documentdb.key")
        String dbKey;

        @Override
        public DocumentClient documentClient() {
            return new DocumentClient(dbUri, dbKey, ConnectionPolicy.GetDefault(), ConsistencyLevel.Session);
        }

        @Override
        public String getDatabase() {
            return "testdb";
        }
    }
}
