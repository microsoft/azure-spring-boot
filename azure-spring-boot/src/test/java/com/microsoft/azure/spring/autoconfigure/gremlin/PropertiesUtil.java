/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.gremlin;

public class PropertiesUtil {

    public static final String PROPERTY_ENDPOINT = "gremlin.endpoint";
    public static final String PROPERTY_PORT = "gremlin.port";
    public static final String PROPERTY_USERNAME = "gremlin.username";
    public static final String PROPERTY_PASSWORD = "gremlin.password";
    public static final String PROPERTY_TELEMETRYALLOWED = "gremlin.telemetryAllowed";

    public static final String GREMLIN_ENDPOINT = "pli-gremlin-3-29.gremlin.cosmosdb.azure.com";
    public static final String GREMLIN_PORT = "443";
    public static final String GREMLIN_USERNAME = "/dbs/pli-database/colls/pli-collection";
    public static final String GREMLIN_PASSWORD = "xxxx";
    public static final String GREMLIN_TELEMETRYALLOWED = "false";

    public static void setProperties() {
        System.setProperty(PROPERTY_ENDPOINT, GREMLIN_ENDPOINT);
        System.setProperty(PROPERTY_PORT, GREMLIN_PORT);
        System.setProperty(PROPERTY_USERNAME, GREMLIN_USERNAME);
        System.setProperty(PROPERTY_PASSWORD, GREMLIN_PASSWORD);
    }

    public static void cleanProperties() {
        System.clearProperty(PROPERTY_ENDPOINT);
        System.clearProperty(PROPERTY_PORT);
        System.clearProperty(PROPERTY_USERNAME);
        System.clearProperty(PROPERTY_PASSWORD);
    }

    public static void setPropertiesWithNoTelemetry() {
        setProperties();

        System.setProperty(PROPERTY_TELEMETRYALLOWED, GREMLIN_TELEMETRYALLOWED);
    }

    public static void cleanPropertiesWithNoTelemetry() {
        cleanProperties();

        System.clearProperty(PROPERTY_TELEMETRYALLOWED);
    }
}
