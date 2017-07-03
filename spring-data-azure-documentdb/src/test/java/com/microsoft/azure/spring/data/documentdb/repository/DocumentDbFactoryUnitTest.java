package com.microsoft.azure.spring.data.documentdb.repository;

import com.microsoft.azure.spring.data.documentdb.core.DocumentDbTemplate;
import com.microsoft.azure.spring.data.documentdb.core.convert.DocumentDbConverter;
import com.microsoft.azure.spring.data.documentdb.core.mapping.DocumentDbPersistentEntity;
import com.microsoft.azure.spring.data.documentdb.repository.domain.Person;
import com.microsoft.azure.spring.data.documentdb.repository.domain.PersonRepository;
import com.microsoft.azure.spring.data.documentdb.repository.support.DocumentDbRepositoryFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.when;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class DocumentDbFactoryUnitTest {
    @Mock
    DocumentDbTemplate template;

    @Mock
    DocumentDbConverter converter;

    @Mock
    DocumentDbPersistentEntity entity;

    @Before
    public void setUp() {
        when(template.getDocumentDbConverter()).thenReturn(converter);
    }

    @Test
    public void createRepositoryWithIdTypeString() {
        when(entity.getType()).thenReturn(Person.class);

        final DocumentDbRepositoryFactory factory = new DocumentDbRepositoryFactory(template, null);

        final PersonRepository repository = factory.getRepository(PersonRepository.class);

        assertThat(repository, is(notNullValue()));
    }
}
