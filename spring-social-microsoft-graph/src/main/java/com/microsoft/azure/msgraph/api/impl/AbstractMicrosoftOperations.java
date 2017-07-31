/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.msgraph.api.impl;

import org.springframework.social.MissingAuthorizationException;

public class AbstractMicrosoftOperations {
    private final boolean isAuthorized;

    public AbstractMicrosoftOperations(boolean isAuthorized) {
        this.isAuthorized = isAuthorized;
    }

    protected void requireAuthorization() {
        if (!isAuthorized) {
            throw new MissingAuthorizationException("microsoft");
        }
    }
}
