/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.msgraph.sample.custom;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Contacts {
    @JsonProperty("value")
    private java.util.List<Contact> contacts;

    @JsonProperty("@odata.nextLink")
    private String nextLink;

    public java.util.List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(java.util.List<Contact> contacts) {
        this.contacts = contacts;
    }

    /**
     * The url to the next page of this collection, or null
     */
    public String getNextLink() {
        return nextLink;
    }

    public void setNextLink(String nextLink) {
        this.nextLink = nextLink;
    }
}
