/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.data.documentdb.repository.core;

import com.microsoft.azure.documentdb.DocumentClient;
import com.microsoft.azure.spring.data.documentdb.DocumentDbFactory;
import com.microsoft.azure.spring.data.documentdb.core.DocumentDbTemplate;
import com.microsoft.azure.spring.data.documentdb.core.convert.DocumentDbConverter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(MockitoJUnitRunner.class)
public class DocumentDbTemplateUnitTest {

    DocumentDbTemplate dbTemplate;

    @Mock
    DocumentDbFactory dbFactory;

    @Mock
    DocumentClient documentClient;

    DocumentDbConverter dbConverter;

    @Before
    public void setUp() {
        this.dbConverter = new DocumentDbConverter();
        this.dbFactory = new DocumentDbFactory(this.documentClient);
        this.dbTemplate = new DocumentDbTemplate(this.dbFactory, this.dbConverter, "testdb");
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectNullDbFactory() throws Exception {
        new DocumentDbTemplate(null, this.dbConverter, "testdb");
    }

    @Test
    public void defaultsConverterToDocumentDbConverter() throws Exception {
        final DocumentDbTemplate template = new DocumentDbTemplate(documentClient, "testdb");
        assertThat(ReflectionTestUtils.getField(template, "dbConverter") instanceof DocumentDbConverter);
    }
}
