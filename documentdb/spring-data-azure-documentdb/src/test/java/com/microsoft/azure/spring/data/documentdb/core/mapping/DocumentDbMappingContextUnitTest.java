/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.data.documentdb.core.mapping;

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
