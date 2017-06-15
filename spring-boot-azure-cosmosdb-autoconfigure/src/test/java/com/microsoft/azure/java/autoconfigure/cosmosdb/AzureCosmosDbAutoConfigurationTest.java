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
        PropertySettingUtil.setProperties();
    }

    @Test
    public void canSetAllPropertiesToDocumentClient() {
        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(AzureCosmosDbAutoConfiguration.class);
        context.refresh();
        final DocumentClient documentClient = context.getBean(DocumentClient.class);

        // No way to verify the setting of key value and ConsistencyLevel.
        final URI uri = documentClient.getServiceEndpoint();
        assertThat(uri.toString()).isEqualTo(PropertySettingUtil.URI);

        final ConnectionPolicy connectionPolicy = documentClient.getConnectionPolicy();
        assertThat(connectionPolicy.getRequestTimeout()).isEqualTo(PropertySettingUtil.REQUEST_TIMEOUT);
        assertThat(connectionPolicy.getMediaRequestTimeout()).isEqualTo(PropertySettingUtil.MEDIA_REQUEST_TIMEOUT);
        assertThat(connectionPolicy.getConnectionMode()).isEqualTo(PropertySettingUtil.CONNECTION_MODE);
        assertThat(connectionPolicy.getMediaReadMode()).isEqualTo(PropertySettingUtil.MEDIA_READ_MODE);
        assertThat(connectionPolicy.getMaxPoolSize()).isEqualTo(PropertySettingUtil.MAX_POOL_SIZE);
        assertThat(connectionPolicy.getIdleConnectionTimeout()).isEqualTo(PropertySettingUtil.IDLE_CONNECTION_TIMEOUT);
        assertThat(connectionPolicy.getUserAgentSuffix()).isEqualTo(PropertySettingUtil.USER_AGENT_SUFFIX);
        assertThat(connectionPolicy.getRetryOptions().getMaxRetryAttemptsOnThrottledRequests()).
                isEqualTo(PropertySettingUtil.RETRY_OPTIONS_MAX_RETRY_ATTEMPS_ON_THROTTLED_REQUESTS);
        assertThat(connectionPolicy.getRetryOptions().getMaxRetryWaitTimeInSeconds()).
                isEqualTo(PropertySettingUtil.RETRY_OPTIONS_MAX_RETRY_WAIT_TIME_IN_SECONDS);
        assertThat(connectionPolicy.getEnableEndpointDiscovery()).
                isEqualTo(PropertySettingUtil.ENABLE_ENDPOINT_DISCOVERY);
        assertThat(connectionPolicy.getPreferredLocations().toString()).
                isEqualTo(PropertySettingUtil.PREFERRED_LOCATIONS.toString());
    }
}
