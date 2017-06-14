/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */
package com.microsoft.azure.java.autoconfigure.cosmosdb;


import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

public class AzureCosmosDbPropertiesTest {
    @BeforeClass
    public static void beforeClass() {
        Utils.setProperties();
    }

    @Test
    public void canSetAllProperties() {
        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(Config.class);
        context.refresh();
        final AzureCosmosDbProperties properties = context.getBean(AzureCosmosDbProperties.class);

        assertThat(properties.getUri()).isEqualTo(Utils.URI);
        assertThat(properties.getKey()).isEqualTo(Utils.KEY);

        assertThat(properties.getConnectionPolicySettings().toConnectionPolicy().getRequestTimeout()).
                isEqualTo(Utils.REQUEST_TIMEOUT);
        assertThat(properties.getConnectionPolicySettings().toConnectionPolicy().getMediaRequestTimeout()).
                isEqualTo(Utils.MEDIA_REQUEST_TIMEOUT);
        assertThat(properties.getConnectionPolicySettings().toConnectionPolicy().getConnectionMode()).
                isEqualTo(Utils.CONNECTION_MODE);
        assertThat(properties.getConnectionPolicySettings().toConnectionPolicy().getMediaReadMode()).
                isEqualTo(Utils.MEDIA_READ_MODE);
        assertThat(properties.getConnectionPolicySettings().toConnectionPolicy().getMaxPoolSize()).
                isEqualTo(Utils.MAX_POOL_SIZE);
        assertThat(properties.getConnectionPolicySettings().toConnectionPolicy().getIdleConnectionTimeout()).
                isEqualTo(Utils.IDLE_CONNECTION_TIMEOUT);
        assertThat(properties.getConnectionPolicySettings().toConnectionPolicy().getUserAgentSuffix()).
                isEqualTo(Utils.USER_AGENT_SUFFIX);
        assertThat(properties.getConnectionPolicySettings().toConnectionPolicy().getRetryOptions().
                getMaxRetryAttemptsOnThrottledRequests()).
                isEqualTo(Utils.RETRY_OPTIONS_MAX_RETRY_ATTEMPS_ON_THROTTLED_REQUESTS);
        assertThat(properties.getConnectionPolicySettings().toConnectionPolicy().getRetryOptions().
                getMaxRetryWaitTimeInSeconds()).
                isEqualTo(Utils.RETRY_OPTIONS_MAX_RETRY_WAIT_TIME_IN_SECONDS);
        assertThat(properties.getConnectionPolicySettings().toConnectionPolicy().getEnableEndpointDiscovery()).
                isEqualTo(Utils.ENABLE_ENDPOINT_DISCOVERY);
        assertThat(properties.getConnectionPolicySettings().toConnectionPolicy().getPreferredLocations().toString()).
                isEqualTo(Utils.PREFERRED_LOCATIONS.toString());

        assertThat(properties.getConsistencyLevel()).isEqualTo(Utils.CONSISTENCY_LEVEL);
    }

    @Configuration
    @EnableConfigurationProperties(AzureCosmosDbProperties.class)
    static class Config {
    }
}
