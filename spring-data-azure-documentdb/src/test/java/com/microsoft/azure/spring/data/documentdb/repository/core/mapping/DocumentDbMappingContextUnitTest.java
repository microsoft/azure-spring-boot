package com.microsoft.azure.spring.data.documentdb.repository.core.mapping;

import com.microsoft.azure.spring.data.documentdb.core.mapping.BasicDocumentDbPersistentEntity;
import com.microsoft.azure.spring.data.documentdb.core.mapping.DocumentDbMappingContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(MockitoJUnitRunner.class)
public class DocumentDbMappingContextUnitTest {

    @Mock
    ApplicationContext context;

    @Test
    public void mappingContextWithImplicitIdProperty() {
        final DocumentDbMappingContext context = new DocumentDbMappingContext();
        final BasicDocumentDbPersistentEntity<?> entity = context.getPersistentEntity(ClassWithId.class);

        assertThat(entity).isNotNull();
    }

    class ClassWithId {
        String field;
        String id;
    }
}
