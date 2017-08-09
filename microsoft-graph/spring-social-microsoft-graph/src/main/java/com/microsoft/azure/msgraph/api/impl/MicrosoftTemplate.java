/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.msgraph.api.impl;

import com.microsoft.azure.msgraph.api.MailOperations;
import com.microsoft.azure.msgraph.api.Microsoft;
import com.microsoft.azure.msgraph.api.UserOperations;
import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.social.support.ClientHttpRequestFactorySelector;
import org.springframework.social.support.URIBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URI;
import java.util.Map;

public class MicrosoftTemplate extends AbstractOAuth2ApiBinding implements Microsoft {
    private static final String MS_GRAPH_BASE_API = "https://graph.microsoft.com/v1.0/";
    private UserOperations userOperations;
    private MailOperations mailOperations;

    public MicrosoftTemplate() {
        initialize();
    }

    public MicrosoftTemplate(String accessToken) {
        super(accessToken);
        initialize();
    }

    public String getGraphAPI(String path) {
        return MS_GRAPH_BASE_API + path;
    }

    public <T> T fetchObject(String objectId, Class<T> type) {
        final URI uri = URIBuilder.fromUri(getGraphAPI(objectId)).build();
        return getRestTemplate().getForObject(uri, type);
    }

    public String postForObject(String objectId, Map<String, Object> data) {
        final URI uri = URIBuilder.fromUri(getGraphAPI(objectId)).build();
        return getRestTemplate().postForObject(uri, data, String.class);
    }

    @Override
    public UserOperations userOperations() {
        return userOperations;
    }

    @Override
    public MailOperations mailOperations() {
        return mailOperations;
    }

    private void initialize() {
        super.setRequestFactory(ClientHttpRequestFactorySelector.bufferRequests(getRestTemplate().getRequestFactory()));
        initSubApis();
    }

    private void initSubApis() {
        userOperations = new UserTemplate(this, isAuthorized());
        mailOperations = new MailTemplate(this, isAuthorized());
    }
}
