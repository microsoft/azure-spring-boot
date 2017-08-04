/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.data.documentdb.core.convert;

import com.google.gson.Gson;
import com.microsoft.azure.documentdb.Document;

public class DocumentDbConverter {

    private final Gson gson;

    public DocumentDbConverter() {
        gson = new Gson();
    }

    public <T> Document convertToDocument(T entity) {
        final String json = this.gson.toJson(entity);

        return new Document(json);
    }

    public <T> T convertFromDocument(Document document, Class<T> entityClass) {
        return this.gson.fromJson(document.toJson(), entityClass);
    }

}
