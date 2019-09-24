/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.mgmt;

import com.microsoft.azure.AzureEnvironment;
import com.microsoft.azure.credentials.ApplicationTokenCredentials;
import com.microsoft.azure.credentials.AzureTokenCredentials;

import java.util.Properties;

public class ClientSecretAccess implements Access {
    
    private static final String TENANT = "azure.tenant";
    private static final String SUBSCRIPTION = "azure.subscription";
    private static final String CLIENT_ID = "azure.client.id";
    private static final String CLIENT_SECRET = "azure.client.secret";
    
    private String tenant;
    private String subscription;
    
    private String clientId;
    private String clientSecret;
    
    public static ClientSecretAccess load() {
        return load(System.getProperties());
    }
    
    public static ClientSecretAccess load(Properties props) {
        final String tenant = props.getProperty(TENANT);
        final String subscription = props.getProperty(SUBSCRIPTION);
        final String clientId = props.getProperty(CLIENT_ID);
        final String clientSecret = props.getProperty(CLIENT_SECRET);
        
        assertNotEmpty(tenant, TENANT);
        assertNotEmpty(subscription, SUBSCRIPTION);
        assertNotEmpty(clientId, CLIENT_ID);
        assertNotEmpty(clientSecret, CLIENT_SECRET);
        
        return new ClientSecretAccess(tenant, subscription, clientId, clientSecret);
    }
    
    private static void assertNotEmpty(String text, String key) {
        if (text == null || text.isEmpty()) {
            throw new RuntimeException(String.format("%s is not set!", key));
        }
    }
    
    public ClientSecretAccess(String tenantId, String subscriptionId, String clientId, String clientSecret) {
        this.tenant = tenantId;
        this.subscription = subscriptionId;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @Override
    public AzureTokenCredentials credentials() {
        return new ApplicationTokenCredentials(
                clientId,
                tenant,
                clientSecret,
                AzureEnvironment.AZURE);
    }

    public String tenant() {
        return tenant;
    }
    
    @Override
    public String subscription() {
        return subscription;
    }
    
    public String clientId() {
        return clientId;
    }
    
    public String clientSecret() {
        return clientSecret;
    }
    
    @Override
    public String servicePrincipal() {
        return clientId;
    }
}
