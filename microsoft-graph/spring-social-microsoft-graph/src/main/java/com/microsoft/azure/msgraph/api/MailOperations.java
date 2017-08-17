/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.msgraph.api;

import org.springframework.util.MultiValueMap;

public interface MailOperations {
    Messages listMessages();

    Messages listMessages(String mailFolder);

    Messages listMessages(MultiValueMap<String, String> params);

    Messages listMessages(String mailFolder, MultiValueMap<String, String> params);

    Messages listMessagesWithNextLink(String nextLink);

    void sendMail(Message message, Boolean saveToSentItems);
}
