/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.msgraph.api.impl;

import com.microsoft.azure.msgraph.api.Microsoft;
import com.microsoft.azure.msgraph.api.UserOperations;
import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.social.support.ClientHttpRequestFactorySelector;

public class MicrosoftTemplate extends AbstractOAuth2ApiBinding implements Microsoft {
    private UserOperations userOperations;

    public MicrosoftTemplate() {
        initialize();
    }

    public MicrosoftTemplate(String accessToken) {
        super(accessToken);
        initialize();
    }

    @Override
    public UserOperations userOperations() {
        return userOperations;
    }

    private void initialize() {
        super.setRequestFactory(ClientHttpRequestFactorySelector.bufferRequests(getRestTemplate().getRequestFactory()));
        initSubApis();
    }

    private void initSubApis() {
        userOperations = new UserTemplate(getRestTemplate(), isAuthorized());
    }
}
