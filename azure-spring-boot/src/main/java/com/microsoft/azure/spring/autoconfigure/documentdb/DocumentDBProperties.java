/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.autoconfigure.documentdb;

import com.microsoft.azure.documentdb.ConsistencyLevel;

import javax.validation.constraints.NotEmpty;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("azure.documentdb")
public class DocumentDBProperties {
    /**
     * Document DB URI.
     */
    @NotEmpty
    private String uri;

    /**
     * Document DB key.
     */
    @NotEmpty
    private String key;

    /**
     * Document DB consistency level.
     */
    private ConsistencyLevel consistencyLevel;

    /**
     * Document DB database name.
     */
    private String database;

    /**
     * Whether allow Microsoft to collect telemetry data.
     */
    private boolean allowTelemetry = true;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String databaseName) {
        this.database = databaseName;
    }

    public ConsistencyLevel getConsistencyLevel() {
        return consistencyLevel;
    }

    public void setConsistencyLevel(ConsistencyLevel consistencyLevel) {
        this.consistencyLevel = consistencyLevel;
    }

    public boolean isAllowTelemetry() {
        return allowTelemetry;
    }

    public void setAllowTelemetry(boolean allowTelemetry) {
        this.allowTelemetry = allowTelemetry;
    }
}
