/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.social.api.impl;

import com.microsoft.azure.spring.social.api.MeOperations;
import com.microsoft.azure.spring.social.api.Microsoft;
import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.social.support.ClientHttpRequestFactorySelector;

public class MicrosoftTemplate extends AbstractOAuth2ApiBinding implements Microsoft {
    private MeOperations meOperations;

    public MicrosoftTemplate() {
        initialize();
    }

    public MicrosoftTemplate(String accessToken) {
        super(accessToken);
        initialize();
    }

    @Override
    public MeOperations meOperations() {
        return meOperations;
    }

    private void initialize() {
        super.setRequestFactory(ClientHttpRequestFactorySelector.bufferRequests(getRestTemplate().getRequestFactory()));
        initSubApis();
    }

    private void initSubApis() {
        meOperations = new MeTemplate(getRestTemplate(), isAuthorized());
    }
}
