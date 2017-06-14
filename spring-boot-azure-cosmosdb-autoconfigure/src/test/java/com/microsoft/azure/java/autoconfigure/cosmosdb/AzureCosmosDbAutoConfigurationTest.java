/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */
package com.microsoft.azure.java.autoconfigure.cosmosdb;


import com.microsoft.azure.documentdb.ConnectionPolicy;
import com.microsoft.azure.documentdb.DocumentClient;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

public class AzureCosmosDbAutoConfigurationTest {
    @BeforeClass
    public static void beforeClass() {
        Utils.setProperties();
    }

    @Test
    public void canSetAllPropertiesToDocumentClient() {
        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(AzureCosmosDbAutoConfiguration.class);
        context.refresh();
        final DocumentClient documentClient = context.getBean(DocumentClient.class);

        // No way to verify the setting of key value and ConsistencyLevel.
        final URI uri = documentClient.getServiceEndpoint();
        assertThat(uri.toString()).isEqualTo(Utils.URI);

        final ConnectionPolicy connectionPolicy = documentClient.getConnectionPolicy();
        assertThat(connectionPolicy.getRequestTimeout()).isEqualTo(Utils.REQUEST_TIMEOUT);
        assertThat(connectionPolicy.getMediaRequestTimeout()).isEqualTo(Utils.MEDIA_REQUEST_TIMEOUT);
        assertThat(connectionPolicy.getConnectionMode()).isEqualTo(Utils.CONNECTION_MODE);
        assertThat(connectionPolicy.getMediaReadMode()).isEqualTo(Utils.MEDIA_READ_MODE);
        assertThat(connectionPolicy.getMaxPoolSize()).isEqualTo(Utils.MAX_POOL_SIZE);
        assertThat(connectionPolicy.getIdleConnectionTimeout()).isEqualTo(Utils.IDLE_CONNECTION_TIMEOUT);
        assertThat(connectionPolicy.getUserAgentSuffix()).isEqualTo(Utils.USER_AGENT_SUFFIX);
        assertThat(connectionPolicy.getRetryOptions().getMaxRetryAttemptsOnThrottledRequests()).
                isEqualTo(Utils.RETRY_OPTIONS_MAX_RETRY_ATTEMPS_ON_THROTTLED_REQUESTS);
        assertThat(connectionPolicy.getRetryOptions().getMaxRetryWaitTimeInSeconds()).
                isEqualTo(Utils.RETRY_OPTIONS_MAX_RETRY_WAIT_TIME_IN_SECONDS);
        assertThat(connectionPolicy.getEnableEndpointDiscovery()).isEqualTo(Utils.ENABLE_ENDPOINT_DISCOVERY);
        assertThat(connectionPolicy.getPreferredLocations().toString()).isEqualTo(Utils.PREFERRED_LOCATIONS.toString());
    }
}
