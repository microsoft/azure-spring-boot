/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.data.documentdb;

import com.microsoft.azure.documentdb.ConnectionPolicy;
import com.microsoft.azure.documentdb.ConsistencyLevel;
import com.microsoft.azure.documentdb.DocumentClient;


public class DocumentDbFactory {

    private DocumentClient documentClient;

    public DocumentDbFactory(String host, String key) {
        documentClient = new DocumentClient(host, key, ConnectionPolicy.GetDefault(), ConsistencyLevel.Session);
    }

    public DocumentDbFactory(DocumentClient client) {
        this.documentClient = client;
    }

    public DocumentClient getDocumentClient() {
        return documentClient;
    }
}
