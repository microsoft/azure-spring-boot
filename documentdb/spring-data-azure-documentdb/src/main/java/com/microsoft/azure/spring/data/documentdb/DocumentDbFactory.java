/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.data.documentdb;

import com.microsoft.azure.documentdb.ConnectionPolicy;
import com.microsoft.azure.documentdb.ConsistencyLevel;
import com.microsoft.azure.documentdb.DocumentClient;
import com.microsoft.azure.spring.common.GetHashMac;
import org.springframework.util.Assert;

public class DocumentDbFactory {

    private static final String USER_AGENT_SUFFIX = "spring-data/0.1.8-SNAPSHOT";

    private DocumentClient documentClient;

    public DocumentDbFactory(String host, String key) {
        this(host, key, true);
    }

    public DocumentDbFactory(String host, String key, boolean isBiEnabled) {
        Assert.hasText(host, "host must not be empty!");
        Assert.hasText(key, "key must not be empty!");

        final ConnectionPolicy policy = ConnectionPolicy.GetDefault();

        String userAgent = ";" + USER_AGENT_SUFFIX;
        if (isBiEnabled && GetHashMac.getHashMac() != null) {
            userAgent += ";" + GetHashMac.getHashMac();
        }
        policy.setUserAgentSuffix(userAgent);

        documentClient = new DocumentClient(host, key, policy, ConsistencyLevel.Session);
    }

    public DocumentDbFactory(DocumentClient client) {
        this.documentClient = client;
    }

    public DocumentClient getDocumentClient() {
        return documentClient;
    }
}
