/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.data.documentdb.repository.autoconfig;

import com.microsoft.azure.documentdb.DocumentClient;
import com.microsoft.azure.spring.data.documentdb.autoconfig.DocumentDbRepositoriesAutoConfiguration;
import com.microsoft.azure.spring.data.documentdb.core.DocumentDbTemplate;
import com.microsoft.azure.spring.data.documentdb.repository.config.EnableDocumentDbRepositories;
import com.microsoft.azure.spring.data.documentdb.repository.domain.Person;
import com.microsoft.azure.spring.data.documentdb.repository.domain.PersonRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

public class DocumentDbRepositoriesAutoConfigurationUnitTest {

    private AnnotationConfigApplicationContext context;

    @MockBean
    private DocumentDbTemplate dbOperations;


    @MockBean
    private DocumentClient documentClient;

    @Before
    public void setup() {
        dbOperations = new DocumentDbTemplate(documentClient, "testdb");
    }

    @After
    public void close() {
        if (this.context != null) {
            this.context.close();
        }
    }

    @Test
    public void testDefaultRepositoryConfiguration() throws Exception {
        prepareApplicationContext(TestConfiguration.class);

        assertThat(this.context.getBean(PersonRepository.class)).isNotNull();
    }

    @Test(expected = NoSuchBeanDefinitionException.class)
    public void autConfigNotKickInIfManualConfigDidNotCreateRepositories() throws Exception {
        prepareApplicationContext(InvalidCustomConfiguration.class);
        this.context.getBean(PersonRepository.class);
    }

    @Configuration
    @TestAutoConfigurationPackage(Person.class)
    protected static class TestConfiguration {

    }

    @Configuration
    @EnableDocumentDbRepositories("foo.bar")
    @TestAutoConfigurationPackage(DocumentDbRepositoriesAutoConfigurationUnitTest.class)
    protected static class InvalidCustomConfiguration {

    }


    private void prepareApplicationContext(Class<?>... configurationClasses) {
        this.context = new AnnotationConfigApplicationContext();
        this.context.register(configurationClasses);
        this.context.register(DocumentDbRepositoriesAutoConfiguration.class);
        this.context.getBeanFactory().registerSingleton(DocumentDbTemplate.class.getName(), dbOperations);
        this.context.refresh();
    }

}

