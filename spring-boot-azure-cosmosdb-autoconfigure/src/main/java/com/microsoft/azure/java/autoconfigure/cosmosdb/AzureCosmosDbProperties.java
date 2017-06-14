/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.java.autoconfigure.cosmosdb;

import com.microsoft.azure.documentdb.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.Collection;

@ConfigurationProperties("azure.cosmosdb")
public class AzureCosmosDbProperties {
    private String uri;
    private String key;

    @NestedConfigurationProperty
    private ConnectionPolicySettings connectionPolicySettings = new ConnectionPolicySettings();

    private ConsistencyLevel consistencyLevel = ConsistencyLevel.Session;


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

    public ConnectionPolicySettings getConnectionPolicySettings() {
        return connectionPolicySettings;
    }

    public void setConnectionPolicySettings(ConnectionPolicySettings connectionPolicySettings) {
        this.connectionPolicySettings = connectionPolicySettings;
    }


    public ConsistencyLevel getConsistencyLevel() {
        return consistencyLevel;
    }

    public void setConsistencyLevel(ConsistencyLevel consistencyLevel) {
        this.consistencyLevel = consistencyLevel;
    }


    public static class ConnectionPolicySettings {

        private int requestTimeout;
        private int mediaRequestTimeout;
        private ConnectionMode connectionMode;
        private MediaReadMode mediaReadMode;
        private int maxPoolSize;
        private int idleConnectionTimeout;
        private String userAgentSuffix;
        @NestedConfigurationProperty
        private RetryOptions retryOptions;
        private boolean enableEndpointDiscovery;
        private Collection<String> preferredLocations;

        // Will not expose HttpsHost related properties, since its settings are complicated.
        // private HttpHost httpHost;

        public ConnectionPolicySettings() {
            final ConnectionPolicy defaultConnectionPolicy = ConnectionPolicy.GetDefault();

            requestTimeout = defaultConnectionPolicy.getRequestTimeout();
            mediaRequestTimeout = defaultConnectionPolicy.getMediaRequestTimeout();
            connectionMode = defaultConnectionPolicy.getConnectionMode();
            mediaReadMode = defaultConnectionPolicy.getMediaReadMode();
            maxPoolSize = defaultConnectionPolicy.getMaxPoolSize();
            idleConnectionTimeout = defaultConnectionPolicy.getIdleConnectionTimeout();
            userAgentSuffix = defaultConnectionPolicy.getUserAgentSuffix();
            retryOptions = defaultConnectionPolicy.getRetryOptions();
            enableEndpointDiscovery = defaultConnectionPolicy.getEnableEndpointDiscovery();
            preferredLocations = defaultConnectionPolicy.getPreferredLocations();
        }

        public ConnectionPolicy toConnectionPolicy() {
            final ConnectionPolicy defaultConnectionPolicy = ConnectionPolicy.GetDefault();

            defaultConnectionPolicy.setRequestTimeout(requestTimeout);
            defaultConnectionPolicy.setMediaRequestTimeout(mediaRequestTimeout);
            defaultConnectionPolicy.setConnectionMode(connectionMode);
            defaultConnectionPolicy.setMediaReadMode(mediaReadMode);
            defaultConnectionPolicy.setMaxPoolSize(maxPoolSize);
            defaultConnectionPolicy.setIdleConnectionTimeout(idleConnectionTimeout);
            defaultConnectionPolicy.setUserAgentSuffix(userAgentSuffix);
            defaultConnectionPolicy.setRetryOptions(retryOptions);
            defaultConnectionPolicy.setEnableEndpointDiscovery(enableEndpointDiscovery);
            defaultConnectionPolicy.setPreferredLocations(preferredLocations);

            return defaultConnectionPolicy;
        }


        public int getRequestTimeout() {
            return this.requestTimeout;
        }

        public void setRequestTimeout(int requestTimeout) {
            this.requestTimeout = requestTimeout;
        }

        public int getMediaRequestTimeout() {
            return this.mediaRequestTimeout;
        }

        public void setMediaRequestTimeout(int mediaRequestTimeout) {
            this.mediaRequestTimeout = mediaRequestTimeout;
        }

        public ConnectionMode getConnectionMode() {
            return this.connectionMode;
        }

        public void setConnectionMode(ConnectionMode connectionMode) {
            this.connectionMode = connectionMode;
        }

        public MediaReadMode getMediaReadMode() {
            return this.mediaReadMode;
        }

        public void setMediaReadMode(MediaReadMode mediaReadMode) {
            this.mediaReadMode = mediaReadMode;
        }

        public int getMaxPoolSize() {
            return this.maxPoolSize;
        }

        public void setMaxPoolSize(int maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
        }

        public int getIdleConnectionTimeout() {
            return this.idleConnectionTimeout;
        }

        public void setIdleConnectionTimeout(int idleConnectionTimeout) {
            this.idleConnectionTimeout = idleConnectionTimeout;
        }

        public String getUserAgentSuffix() {
            return this.userAgentSuffix;
        }

        public void setUserAgentSuffix(String userAgentSuffix) {
            this.userAgentSuffix = userAgentSuffix;
        }

        public RetryOptions getRetryOptions() {
            return retryOptions;
        }

        public void setConnectionPolicesRetryOptions(RetryOptions retryOptions) {
            this.retryOptions = retryOptions;
        }

        public boolean getEnableEndpointDiscovery() {
            return this.enableEndpointDiscovery;
        }

        public void setEnableEndpointDiscovery(boolean enableEndpointDiscovery) {
            this.enableEndpointDiscovery = enableEndpointDiscovery;
        }

        public Collection<String> getPreferredLocations() {
            return this.preferredLocations;
        }

        public void setPreferredLocations(Collection<String> preferredLocations) {
            this.preferredLocations = preferredLocations;
        }
    }
}
