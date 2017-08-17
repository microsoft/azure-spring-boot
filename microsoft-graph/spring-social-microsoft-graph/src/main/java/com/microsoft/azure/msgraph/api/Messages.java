/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.msgraph.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Messages {
    @JsonProperty("@odata.nextLink")
    private String nextLink;

    private java.util.List<Message> value;

    public java.util.List<Message> getValue() {
        return value;
    }

    public void setValue(java.util.List<Message> value) {
        this.value = value;
    }

    public String getNextLink() {
        return nextLink;
    }

    public void setNextLink(String nextLink) {
        this.nextLink = nextLink;
    }
}
