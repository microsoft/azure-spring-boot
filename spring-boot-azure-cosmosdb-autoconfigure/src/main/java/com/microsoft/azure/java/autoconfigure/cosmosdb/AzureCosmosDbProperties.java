/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.java.autoconfigure.cosmosdb;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("azure.cosmosdb")
public class AzureCosmosDbProperties {
    private String uri;
    private String key;

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
}
