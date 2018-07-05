/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.gremlin;

public class PropertiesUtil {
    private static final String PROPERTY_ENDPOINT = "gremlin.endpoint";
    private static final String PROPERTY_PORT = "gremlin.port";
    private static final String PROPERTY_USERNAME = "gremlin.username";
    private static final String PROPERTY_PASSWORD = "gremlin.password";
    private static final String PROPERTY_TELEMETRY = "gremlin.telemetryAllowed";

    public static final String ENDPOINT = "pli-gremlin-3-29.gremlin.cosmosdb.azure.com";
    public static final String PORT = "443";
    public static final String USERNAME = "/dbs/pli-database/colls/pli-collection";
    public static final String PASSWORD = "xxxx";

    public static final String GREMLIN_ENDPOINT_CONFIG = PROPERTY_ENDPOINT + "=" + ENDPOINT;
    public static final String GREMLIN_PORT_CONFIG = PROPERTY_PORT + "=" + PORT;
    public static final String GREMLIN_USERNAME_CONFIG = PROPERTY_USERNAME + "=" + USERNAME;
    public static final String GREMLIN_PASSWORD_CONFIG = PROPERTY_PASSWORD + "=" + PASSWORD;
    public static final String GREMLIN_TELEMETRY_CONFIG_NOT_ALLOWED = PROPERTY_TELEMETRY + "=" + "false";
    public static final String GREMLIN_TELEMETRY_CONFIG_ALLOWED = PROPERTY_TELEMETRY + "=" + "true";
}
