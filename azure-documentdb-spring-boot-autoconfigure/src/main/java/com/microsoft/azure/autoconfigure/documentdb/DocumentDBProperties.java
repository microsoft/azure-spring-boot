/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the Apache License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.autoconfigure.documentdb;

import com.microsoft.azure.documentdb.ConsistencyLevel;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("azure.documentdb")
public class DocumentDBProperties {
    private String uri;
    private String key;
    private ConsistencyLevel consistencyLevel;
    private String database;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getKey() {
        return key;
    }

    public String getDatabase() {
        return database;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ConsistencyLevel getConsistencyLevel() {
        return consistencyLevel;
    }

    public void setConsistencyLevel(ConsistencyLevel consistencyLevel) {
        this.consistencyLevel = consistencyLevel;
    }

    public void setDatabase(String databaseName) {
        this.database = databaseName;
    }

}
