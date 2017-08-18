/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.msgraph.api.impl;

import com.microsoft.azure.msgraph.api.impl.custom.Contact;
import com.microsoft.azure.msgraph.api.impl.custom.Contacts;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class CustomTemplateTest extends AbstractMicrosoftApiTest {
    @Test
    public void getContactsSuccessfully() {
        mockServer
                .expect(requestTo(microsoft.getGraphAPI("me/contacts")))
                .andExpect(method(GET))
                .andExpect(header("Authorization", "Bearer access token"))
                .andRespond(withSuccess(jsonResource("contacts"), MediaType.APPLICATION_JSON));

        final RestTemplate restTemplate = microsoft.customOperations().getRestTemplate();
        final URI uri = microsoft.customOperations().getGraphAPIURI("me/contacts");
        final Contacts contacts = restTemplate.getForObject(uri, Contacts.class);

        assertThat(contacts.getContacts()).size().isEqualTo(10);
        assertThat(contacts.getNextLink()).isEqualTo("https://graph.microsoft.com/v1.0/me/contacts?$skip=10");

        final Contact contact = contacts.getContacts().get(0);
        assertThat(contact.getDisplayName()).isEqualTo("John Doe");
        assertThat(contact.getMobilePhone()).isNull();
    }
}
