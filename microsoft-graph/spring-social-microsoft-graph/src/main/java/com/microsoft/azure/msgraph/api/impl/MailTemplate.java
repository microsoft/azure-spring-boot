/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.msgraph.api.impl;

import com.microsoft.azure.msgraph.api.MailOperations;
import com.microsoft.azure.msgraph.api.Message;
import com.microsoft.azure.msgraph.api.Messages;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

public class MailTemplate extends AbstractMicrosoftOperations implements MailOperations {
    private final MicrosoftTemplate microsoft;

    public MailTemplate(MicrosoftTemplate microsoft, boolean authorized) {
        super(authorized);
        this.microsoft = microsoft;
    }

    @Override
    public Messages listMessages() {
        return listMessages(null, null);
    }

    @Override
    public Messages listMessages(String mailFolder) {
        return listMessages(mailFolder, null);
    }

    @Override
    public Messages listMessages(MultiValueMap<String, String> params) {
        return listMessages(null, params);
    }

    @Override
    public Messages listMessages(String mailFolder, MultiValueMap<String, String> params) {
        String uri = null;
        if (mailFolder == null) {
            uri = "me/messages";
        } else {
            uri = "me/mailFolders/" + mailFolder + "/messages";
        }

        if (params != null) {
            return microsoft.fetchObject(uri, params, Messages.class);
        } else {
            return microsoft.fetchObject(uri, Messages.class);
        }
    }

    @Override
    public Messages listMessagesWithNextLink(String nextLink) {
        return microsoft.fetchObjectWithAbsolutePath(nextLink, Messages.class);
    }

    @Override
    public void sendMail(Message message, Boolean saveToSentItems) {
        final Map<String, Object> data = new HashMap<>();
        data.put("message", message);
        data.put("saveToSentItems", saveToSentItems);

        microsoft.postForObject("me/sendMail", data);
    }
}
