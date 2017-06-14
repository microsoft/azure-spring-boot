/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */
package com.microsoft.azure.java.autoconfigure.cosmosdb;

import com.microsoft.azure.documentdb.ConnectionMode;
import com.microsoft.azure.documentdb.ConsistencyLevel;
import com.microsoft.azure.documentdb.MediaReadMode;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

public class Utils {
    public static final String URI = "https://test.documents.azure.com:443/";
    public static final String KEY = "KeyString";

    public static final int REQUEST_TIMEOUT = 4;
    public static final int MEDIA_REQUEST_TIMEOUT = 3;
    public static final ConnectionMode CONNECTION_MODE = ConnectionMode.DirectHttps;
    public static final MediaReadMode MEDIA_READ_MODE = MediaReadMode.Streamed;
    public static final int MAX_POOL_SIZE = 1;
    public static final int IDLE_CONNECTION_TIMEOUT = 2;
    public static final String USER_AGENT_SUFFIX = "suffix";
    public static final int RETRY_OPTIONS_MAX_RETRY_ATTEMPS_ON_THROTTLED_REQUESTS = 5;
    public static final int RETRY_OPTIONS_MAX_RETRY_WAIT_TIME_IN_SECONDS = 6;
    public static final boolean ENABLE_ENDPOINT_DISCOVERY = false;
    public static final List<String> PREFERRED_LOCATIONS = Arrays.asList("East US", "West US", "North Europe");

    public static final ConsistencyLevel CONSISTENCY_LEVEL = ConsistencyLevel.Strong;

    public static void setProperties() {
        System.setProperty("azure.cosmosdb.uri", URI);
        System.setProperty("azure.cosmosdb.key", KEY);

        System.setProperty("azure.cosmosdb.connection-policy-settings.request-timeout",
                Integer.toString(REQUEST_TIMEOUT));
        System.setProperty("azure.cosmosdb.connection-policy-settings.media-request-timeout",
                Integer.toString(MEDIA_REQUEST_TIMEOUT));
        System.setProperty("azure.cosmosdb.connection-policy-settings.connection-mode", CONNECTION_MODE.name());
        System.setProperty("azure.cosmosdb.connection-policy-settings.media-read-mode", MEDIA_READ_MODE.name());
        System.setProperty("azure.cosmosdb.connection-policy-settings.max-pool-size", Integer.toString(MAX_POOL_SIZE));
        System.setProperty("azure.cosmosdb.connection-policy-settings.idle-connection-timeout",
                Integer.toString(IDLE_CONNECTION_TIMEOUT));
        System.setProperty("azure.cosmosdb.connection-policy-settings.user-agent-suffix", USER_AGENT_SUFFIX);
        System.setProperty(
                "azure.cosmosdb.connection-policy-settings.retry-options.max-retry-attempts-on-throttled-requests",
                Integer.toString(RETRY_OPTIONS_MAX_RETRY_ATTEMPS_ON_THROTTLED_REQUESTS));
        System.setProperty("azure.cosmosdb.connection-policy-settings.retry-options.max-retry-wait-time-in-seconds",
                Integer.toString(RETRY_OPTIONS_MAX_RETRY_WAIT_TIME_IN_SECONDS));
        System.setProperty("azure.cosmosdb.connection-policy-settings.enable-endpoint-discovery",
                Boolean.toString(ENABLE_ENDPOINT_DISCOVERY));
        System.setProperty("azure.cosmosdb.connection-policy-settings.preferred-locations",
                StringUtils.collectionToDelimitedString(PREFERRED_LOCATIONS, ","));

        System.setProperty("azure.cosmosdb.consistency-level", CONSISTENCY_LEVEL.name());
    }
}
