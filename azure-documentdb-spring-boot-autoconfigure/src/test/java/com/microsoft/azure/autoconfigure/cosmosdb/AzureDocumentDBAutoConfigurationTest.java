/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */
package com.microsoft.azure.autoconfigure.documentdb;


import com.microsoft.azure.documentdb.ConnectionPolicy;
import com.microsoft.azure.documentdb.DocumentClient;
import com.microsoft.azure.documentdb.RetryOptions;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

public class AzureDocumentDBAutoConfigurationTest {
    @BeforeClass
    public static void beforeClass() {
        PropertySettingUtil.setProperties();
    }

    @Test
    public void canSetAllPropertiesToDocumentClient() {
        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(AzureDocumentDBAutoConfiguration.class);
        context.refresh();
        final DocumentClient documentClient = context.getBean(DocumentClient.class);

        // No way to verify the setting of key value and ConsistencyLevel.
        final URI uri = documentClient.getServiceEndpoint();
        assertThat(uri.toString()).isEqualTo(PropertySettingUtil.URI);

        assertThat(documentClient.getConnectionPolicy()).isEqualTo(ConnectionPolicy.GetDefault());
    }

    @Test
    public void canSetConnectionPolicyToDocumentClient(){
        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(AzureDocumentDBAutoConfiguration.class, ConnectionPolicyConfig.class);
        context.refresh();
        final DocumentClient documentClient = context.getBean(DocumentClient.class);

        final ConnectionPolicy connectionPolicy = documentClient.getConnectionPolicy();
        assertThat(connectionPolicy.getRequestTimeout()).isEqualTo(PropertySettingUtil.REQUEST_TIMEOUT);
        assertThat(connectionPolicy.getMediaRequestTimeout()).isEqualTo(PropertySettingUtil.MEDIA_REQUEST_TIMEOUT);
        assertThat(connectionPolicy.getConnectionMode()).isEqualTo(PropertySettingUtil.CONNECTION_MODE);
        assertThat(connectionPolicy.getMediaReadMode()).isEqualTo(PropertySettingUtil.MEDIA_READ_MODE);
        assertThat(connectionPolicy.getMaxPoolSize()).isEqualTo(PropertySettingUtil.MAX_POOL_SIZE);
        assertThat(connectionPolicy.getIdleConnectionTimeout()).isEqualTo(PropertySettingUtil.IDLE_CONNECTION_TIMEOUT);
        assertThat(connectionPolicy.getUserAgentSuffix()).isEqualTo(PropertySettingUtil.USER_AGENT_SUFFIX);
        assertThat(connectionPolicy.getRetryOptions().getMaxRetryAttemptsOnThrottledRequests()).
                isEqualTo(PropertySettingUtil.RETRY_OPTIONS_MAX_RETRY_ATTEMPTS_ON_THROTTLED_REQUESTS);
        assertThat(connectionPolicy.getRetryOptions().getMaxRetryWaitTimeInSeconds()).
                isEqualTo(PropertySettingUtil.RETRY_OPTIONS_MAX_RETRY_WAIT_TIME_IN_SECONDS);
        assertThat(connectionPolicy.getEnableEndpointDiscovery()).
                isEqualTo(PropertySettingUtil.ENABLE_ENDPOINT_DISCOVERY);
        assertThat(connectionPolicy.getPreferredLocations().toString()).
                isEqualTo(PropertySettingUtil.PREFERRED_LOCATIONS.toString());
    }

    @Configuration
    static class ConnectionPolicyConfig {
        @Bean
        public ConnectionPolicy connectionPolicy(){
            final ConnectionPolicy connectionPolicy = ConnectionPolicy.GetDefault();

            connectionPolicy.setRequestTimeout(PropertySettingUtil.REQUEST_TIMEOUT);
            connectionPolicy.setMediaRequestTimeout(PropertySettingUtil.MEDIA_REQUEST_TIMEOUT);
            connectionPolicy.setConnectionMode(PropertySettingUtil.CONNECTION_MODE);
            connectionPolicy.setMediaReadMode(PropertySettingUtil.MEDIA_READ_MODE);
            connectionPolicy.setMaxPoolSize(PropertySettingUtil.MAX_POOL_SIZE);
            connectionPolicy.setIdleConnectionTimeout(PropertySettingUtil.IDLE_CONNECTION_TIMEOUT);
            connectionPolicy.setUserAgentSuffix(PropertySettingUtil.USER_AGENT_SUFFIX);

            final RetryOptions retryOptions = new RetryOptions();
            retryOptions.setMaxRetryAttemptsOnThrottledRequests(
                    PropertySettingUtil.RETRY_OPTIONS_MAX_RETRY_ATTEMPTS_ON_THROTTLED_REQUESTS);
            retryOptions.setMaxRetryWaitTimeInSeconds(
                    PropertySettingUtil.RETRY_OPTIONS_MAX_RETRY_WAIT_TIME_IN_SECONDS);
            connectionPolicy.setRetryOptions(retryOptions);

            connectionPolicy.setEnableEndpointDiscovery(PropertySettingUtil.ENABLE_ENDPOINT_DISCOVERY);
            connectionPolicy.setPreferredLocations(PropertySettingUtil.PREFERRED_LOCATIONS);

            return connectionPolicy;
        }
    }
}
