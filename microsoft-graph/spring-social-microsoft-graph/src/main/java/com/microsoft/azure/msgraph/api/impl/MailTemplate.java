/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.msgraph.api.impl;

import com.microsoft.azure.msgraph.api.MailOperations;
import com.microsoft.azure.msgraph.api.Messages;

public class MailTemplate extends AbstractMicrosoftOperations implements MailOperations {
    private final MicrosoftTemplate microsoft;

    public MailTemplate(MicrosoftTemplate microsoft, boolean authorized) {
        super(authorized);
        this.microsoft = microsoft;
    }

    @Override
    public Messages listMessages(String mailFolder) {
        if (mailFolder == null) {
            mailFolder = "Inbox";
        }
        return microsoft.fetchObject("me/mailFolders/" + mailFolder + "/messages/", Messages.class);
    }
}
