/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.msgraph.api.impl;

import com.microsoft.azure.msgraph.api.User;
import com.microsoft.azure.msgraph.api.UserOperations;

public class UserTemplate extends AbstractMicrosoftOperations implements UserOperations {
    private final MicrosoftTemplate microsoft;

    public UserTemplate(MicrosoftTemplate microsoft, boolean authorized) {
        super(authorized);
        this.microsoft = microsoft;
    }

    @Override
    public User getUserProfile() {
        return microsoft.fetchObject("me", User.class);
    }
}
