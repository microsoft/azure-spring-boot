/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.msgraph.api.impl;

import org.springframework.social.MissingAuthorizationException;
import org.springframework.social.support.URIBuilder;
import org.springframework.util.MultiValueMap;

import java.net.URI;

public class AbstractMicrosoftOperations {
    /**
     * Microsoft Graph base API
     */
    private static final String API_URL_BASE = "https://graph.microsoft.com/v1.0/";
    private final boolean isAuthorized;

    public AbstractMicrosoftOperations(boolean isAuthorized) {
        this.isAuthorized = isAuthorized;
    }

    protected void requireAuthorization() {
        if (!isAuthorized) {
            throw new MissingAuthorizationException("microsoft");
        }
    }

    protected String buildUri(String path) {
        return API_URL_BASE + path;
    }

    public URI buildUri(String path, String name, String value) {
        return URIBuilder.fromUri(API_URL_BASE + path).queryParam(name, value).build();
    }

    public URI buildUri(String path, MultiValueMap<String, String> queryParams) {
        return URIBuilder.fromUri(API_URL_BASE + path).queryParams(queryParams).build();
    }
}
