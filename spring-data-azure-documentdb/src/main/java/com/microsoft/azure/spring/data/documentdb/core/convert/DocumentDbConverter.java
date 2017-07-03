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

    // todo: implement converter based on Spring data converter
    public <T> T convertFromDocument(Document document, Class<T> entityClass) {
        return this.gson.fromJson(document.toJson(), entityClass);
    }
}
