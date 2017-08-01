/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.data.documentdb.repository.core.converter;


import com.microsoft.azure.documentdb.Document;
import com.microsoft.azure.spring.data.documentdb.core.convert.DocumentDbConverter;
import com.microsoft.azure.spring.data.documentdb.repository.domain.Person;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DocumentDbConverterUnitTest {

    private static final String id = "testId";
    private static final String firstName = "testFirstName";
    private static final String lastName = "testLastName";
    private static final String idPropertyName = "id";
    private static final String firstNamePropertyName = "firstName";
    private static final String lastNamePropertyName = "lastName";
    private DocumentDbConverter dbConverter;

    @Before
    public void setup() {
        dbConverter = new DocumentDbConverter();
    }

    @Test
    public void testConvertFromEntityToDocument() {
        final Person person = new Person(id, firstName, lastName);
        final Document document = dbConverter.convertToDocument(person);

        assertThat(document.has(idPropertyName)).isTrue();
        assertThat(document.has(firstNamePropertyName)).isTrue();
        assertThat(document.has(lastNamePropertyName)).isTrue();
        assertThat(document.getId()).isEqualTo(id);
        assertThat(document.getString(firstNamePropertyName)).isEqualTo(firstName);
        assertThat(document.getString(lastNamePropertyName)).isEqualTo(lastName);
    }

    @Test
    public void testConvertFromDocumentToEntity() {
        final JSONObject json = new JSONObject();
        json.put(idPropertyName, id);
        json.put(firstNamePropertyName, firstName);
        json.put(lastNamePropertyName, lastName);

        final Document document = new Document(JSONObject.valueToString(json));
        final Person person = dbConverter.convertFromDocument(document, Person.class);
        assertThat(person.getId()).isEqualTo(id);
        assertThat(person.getFirstName()).isEqualTo(firstName);
        assertThat(person.getLastName()).isEqualTo(lastName);

    }
}
