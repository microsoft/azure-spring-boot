/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.data.documentdb.core;

import com.microsoft.azure.documentdb.DocumentClient;
import com.microsoft.azure.spring.data.documentdb.DocumentDbFactory;
import com.microsoft.azure.spring.data.documentdb.core.convert.MappingDocumentDbConverter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class DocumentDbTemplateUnitTest {

    DocumentDbTemplate dbTemplate;

    @Mock
    DocumentClient documentClient;

    @Mock
    MappingDocumentDbConverter dbConverter;

    @Before
    public void setUp() {
        this.dbTemplate = new DocumentDbTemplate(new DocumentDbFactory(documentClient), dbConverter, "testdb");

    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectNullDbFactory() throws Exception {
        new DocumentDbTemplate(documentClient, null, "testdb");
    }
}
