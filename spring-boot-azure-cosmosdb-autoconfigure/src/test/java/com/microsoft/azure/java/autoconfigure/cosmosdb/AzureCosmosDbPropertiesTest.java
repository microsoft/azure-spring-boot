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
        PropertySettingUtil.setProperties();
    }

    @Test
    public void canSetAllProperties() {
        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(Config.class);
        context.refresh();
        final AzureCosmosDbProperties properties = context.getBean(AzureCosmosDbProperties.class);

        assertThat(properties.getUri()).isEqualTo(PropertySettingUtil.URI);
        assertThat(properties.getKey()).isEqualTo(PropertySettingUtil.KEY);

        assertThat(properties.getConnectionPolicySettings().toConnectionPolicy().getRequestTimeout()).
                isEqualTo(PropertySettingUtil.REQUEST_TIMEOUT);
        assertThat(properties.getConnectionPolicySettings().toConnectionPolicy().getMediaRequestTimeout()).
                isEqualTo(PropertySettingUtil.MEDIA_REQUEST_TIMEOUT);
        assertThat(properties.getConnectionPolicySettings().toConnectionPolicy().getConnectionMode()).
                isEqualTo(PropertySettingUtil.CONNECTION_MODE);
        assertThat(properties.getConnectionPolicySettings().toConnectionPolicy().getMediaReadMode()).
                isEqualTo(PropertySettingUtil.MEDIA_READ_MODE);
        assertThat(properties.getConnectionPolicySettings().toConnectionPolicy().getMaxPoolSize()).
                isEqualTo(PropertySettingUtil.MAX_POOL_SIZE);
        assertThat(properties.getConnectionPolicySettings().toConnectionPolicy().getIdleConnectionTimeout()).
                isEqualTo(PropertySettingUtil.IDLE_CONNECTION_TIMEOUT);
        assertThat(properties.getConnectionPolicySettings().toConnectionPolicy().getUserAgentSuffix()).
                isEqualTo(PropertySettingUtil.USER_AGENT_SUFFIX);
        assertThat(properties.getConnectionPolicySettings().toConnectionPolicy().getRetryOptions().
                getMaxRetryAttemptsOnThrottledRequests()).
                isEqualTo(PropertySettingUtil.RETRY_OPTIONS_MAX_RETRY_ATTEMPS_ON_THROTTLED_REQUESTS);
        assertThat(properties.getConnectionPolicySettings().toConnectionPolicy().getRetryOptions().
                getMaxRetryWaitTimeInSeconds()).
                isEqualTo(PropertySettingUtil.RETRY_OPTIONS_MAX_RETRY_WAIT_TIME_IN_SECONDS);
        assertThat(properties.getConnectionPolicySettings().toConnectionPolicy().getEnableEndpointDiscovery()).
                isEqualTo(PropertySettingUtil.ENABLE_ENDPOINT_DISCOVERY);
        assertThat(properties.getConnectionPolicySettings().toConnectionPolicy().getPreferredLocations().toString()).
                isEqualTo(PropertySettingUtil.PREFERRED_LOCATIONS.toString());

        assertThat(properties.getConsistencyLevel()).isEqualTo(PropertySettingUtil.CONSISTENCY_LEVEL);
    }

    @Configuration
    @EnableConfigurationProperties(AzureCosmosDbProperties.class)
    static class Config {
    }
}
