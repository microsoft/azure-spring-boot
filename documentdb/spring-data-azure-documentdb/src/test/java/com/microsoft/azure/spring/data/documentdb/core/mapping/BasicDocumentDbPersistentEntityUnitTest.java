/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.data.documentdb.core.mapping;

import com.microsoft.azure.spring.data.documentdb.domain.Person;
import org.junit.Test;
import org.springframework.data.util.ClassTypeInformation;

import static org.assertj.core.api.Assertions.assertThat;

public class BasicDocumentDbPersistentEntityUnitTest {

    @Test
    public void testGetCollection() {
        final BasicDocumentDbPersistentEntity entity = new BasicDocumentDbPersistentEntity<Person>(
                ClassTypeInformation.from(Person.class));
        assertThat(entity.getCollection()).isEqualTo("");
    }

    @Test
    public void testGetLanguage() {
        final BasicDocumentDbPersistentEntity entity = new BasicDocumentDbPersistentEntity<Person>(
                ClassTypeInformation.from(Person.class));
        assertThat(entity.getLanguage()).isEqualTo("");
    }

}
