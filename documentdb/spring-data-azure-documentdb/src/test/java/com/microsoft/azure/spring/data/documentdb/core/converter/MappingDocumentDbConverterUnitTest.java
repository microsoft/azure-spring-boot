/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.data.documentdb.core.converter;

import com.microsoft.azure.documentdb.Document;
import com.microsoft.azure.spring.data.documentdb.core.convert.MappingDocumentDbConverter;
import com.microsoft.azure.spring.data.documentdb.core.mapping.DocumentDbMappingContext;
import com.microsoft.azure.spring.data.documentdb.domain.Address;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class MappingDocumentDbConverterUnitTest {

    MappingDocumentDbConverter dbConverter;

    DocumentDbMappingContext mappingContext;

    @Mock
    ApplicationContext applicationContext;


    @Before
    public void setup() {
        mappingContext = new DocumentDbMappingContext();
        mappingContext.setApplicationContext(applicationContext);
        mappingContext.afterPropertiesSet();

        mappingContext.getPersistentEntity(Address.class);

        dbConverter = new MappingDocumentDbConverter(mappingContext);
    }


    @Test
    public void covertAddressToDocumentCorrectly() {
        final Address testAddress = new Address("98052", "testCity", "testStreet");

        final Document document = new Document();

        dbConverter.write(testAddress, document);

        assertThat(document.getId()).isEqualTo(testAddress.getPostalCode());
        assertThat(document.getString("city")).isEqualTo(testAddress.getCity());
        assertThat(document.getString("street")).isEqualTo(testAddress.getStreet());

    }

    @Test
    public void convertDocumentToAddressCorrectly() {
        final Document document = new Document();

        document.setId("testId");
        document.set("city", "testCity");
        document.set("street", "testStreet");

        final Address address = dbConverter.read(Address.class, document);

        assertThat(address.getPostalCode()).isEqualTo("testId");
        assertThat(address.getCity()).isEqualTo("testCity");
        assertThat(address.getStreet()).isEqualTo("testStreet");
    }
}

