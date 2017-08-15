/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.msgraph.api.impl;

import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.microsoft.azure.msgraph.api.Importance;
import com.microsoft.azure.msgraph.api.Message;
import com.microsoft.azure.msgraph.api.Messages;
import org.junit.Test;
import org.springframework.http.MediaType;

import java.text.ParseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class MailTemplateTest extends AbstractMicrosoftApiTest {

    @Test
    public void listMessagesSuccessfully() throws ParseException {
        mockServer
                .expect(requestTo(microsoft.getGraphAPI("me/mailFolders/Inbox/messages/")))
                .andExpect(method(GET))
                .andExpect(header("Authorization", "Bearer access token"))
                .andRespond(withSuccess(jsonResource("messages"), MediaType.APPLICATION_JSON));

        final Messages messages = microsoft.mailOperations().listMessages(null);
        assertThat(messages.getValue().size()).isEqualTo(2);

        final Message message = messages.getValue().get(0);
        assertThat(message.getCreatedDateTime().getTime()).isEqualTo(new StdDateFormat().parse("2017-07-31T16:20:47Z"));
        assertThat(message.getLastModifiedDateTime().getTime()).isEqualTo(new StdDateFormat().parse("2017-08-08T19:40:47Z"));
        assertThat(message.getChangeKey()).isEqualTo("CQAAABYAAABuW0RzxJhUT4ez5BWQCxI9AABh8vwG");
        assertThat(message.getCategories()).isEmpty();
        assertThat(message.getReceivedDateTime().getTime()).isEqualTo(new StdDateFormat().parse("2017-07-31T16:20:47Z"));
        assertThat(message.getSentDateTime().getTime()).isEqualTo(new StdDateFormat().parse("2017-07-31T11:17:11Z"));
        assertThat(message.getHasAttachments()).isFalse();
        assertThat(message.getInternetMessageId()).isEqualTo("<CY1PR21MB0037008DE4635704988C7178A6B20@CY1PR21MB0037.namprd21.prod.outlook.com>");
        assertThat(message.getSubject()).isEqualTo("Weekly digest: Office 365 changes");
        assertThat(message.getBodyPreview()).isEqualTo("You have 3 new Office 365 Message center announcements from last week\r\n\r\n\r\nOrganization: MICROSOFT API SANDBOX\r\n        Is this digest useful to you?   Yes     No      Tell us more...\r\nOffice 365 announcements from last week         Edit Message cente");
        assertThat(message.getImportance()).isEqualTo(Importance.normal);
        assertThat(message.getParentFolderId()).isEqualTo("AQMkADRmZWM1ODE4LWQ4YWItNDlkYS1iZTY4LWVhZWEzYjRlODgAMzkALgAAAwuZXteXVEJLqYcO_ELlrI4BAG5bRHPEmFRPh7PkFZALEj0AAAIBDAAAAA==");
        assertThat(message.getConversationId()).isEqualTo("AAQkADRmZWM1ODE4LWQ4YWItNDlkYS1iZTY4LWVhZWEzYjRlODgzOQAQANhHWVZXoHtImHvs-m5jEbU=");
        assertThat(message.getIsDeliveryReceiptRequested()).isNull();
        assertThat(message.getIsReadReceiptRequested()).isFalse();
        assertThat(message.getIsRead()).isFalse();
        assertThat(message.getIsDraft()).isFalse();
        assertThat(message.getWebLink()).isEqualTo("https://outlook.office365.com/owa/?ItemID=AAMkADRmZWM1ODE4LWQ4YWItNDlkYS1iZTY4LWVhZWEzYjRlODgzOQBGAAAAAAALmV7Xl1RCS6mHDvhC5ayOBwBuW0RzxJhUT4ez5BWQCxI9AAAAAAEMAABuW0RzxJhUT4ez5BWQCxI9AABdGgEMAAA%3D&exvsurl=1&viewmodel=ReadMessageItem");
    }
}
