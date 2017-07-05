/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.data.documentdb.core.mapping;

import org.springframework.data.mapping.PersistentEntity;


public interface DocumentDbPersistentEntity<T> extends PersistentEntity<T, DocumentDbPersistentProperty> {

    String getCollection();

    String getLanguage();
}
