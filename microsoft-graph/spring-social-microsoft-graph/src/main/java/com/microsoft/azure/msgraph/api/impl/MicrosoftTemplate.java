/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.msgraph.api.impl;

import com.microsoft.azure.msgraph.api.CustomOperations;
import com.microsoft.azure.msgraph.api.MailOperations;
import com.microsoft.azure.msgraph.api.Microsoft;
import com.microsoft.azure.msgraph.api.UserOperations;
import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.social.support.ClientHttpRequestFactorySelector;
import org.springframework.social.support.URIBuilder;

import java.net.URI;
import java.util.Map;

public class MicrosoftTemplate extends AbstractOAuth2ApiBinding implements Microsoft {
    private static final String MS_GRAPH_BASE_API = "https://graph.microsoft.com/";
    private String apiVersion = "v1.0";
    private UserOperations userOperations;
    private MailOperations mailOperations;
    private CustomOperations customOperations;

    public MicrosoftTemplate() {
        initialize();
    }

    public MicrosoftTemplate(String accessToken) {
        super(accessToken);
        initialize();
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getGraphAPI(String relativePath) {
        return MS_GRAPH_BASE_API + apiVersion + "/" + relativePath;
    }

    public URI getGraphAPIURI(String relativePath) {
        return URIBuilder.fromUri(getGraphAPI(relativePath)).build();
    }

    public <T> T fetchObject(String objectId, Class<T> type) {
        return getRestTemplate().getForObject(getGraphAPIURI(objectId), type);
    }

    public String postForObject(String objectId, Map<String, Object> data) {
        return getRestTemplate().postForObject(getGraphAPIURI(objectId), data, String.class);
    }

    @Override
    public UserOperations userOperations() {
        return userOperations;
    }

    @Override
    public MailOperations mailOperations() {
        return mailOperations;
    }

    @Override
    public CustomOperations customOperations() {
        return customOperations;
    }

    private void initialize() {
        super.setRequestFactory(ClientHttpRequestFactorySelector.bufferRequests(getRestTemplate().getRequestFactory()));
        initSubApis();
    }

    private void initSubApis() {
        userOperations = new UserTemplate(this, isAuthorized());
        mailOperations = new MailTemplate(this, isAuthorized());
        customOperations = new CustomTemplate(this, isAuthorized());
    }
}
