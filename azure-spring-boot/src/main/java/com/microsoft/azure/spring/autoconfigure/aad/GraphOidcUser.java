/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.aad;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class GraphOidcUser implements OidcUser {
    private final OidcUser delegate;
    private final String graphApiToken;

    public GraphOidcUser(OidcUser delegate, String token) {
        this.delegate = delegate;
        this.graphApiToken = token;
    }

    @Override
    public Map<String, Object> getClaims() {
        return this.delegate.getClaims();
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return this.delegate.getUserInfo();
    }

    @Override
    public OidcIdToken getIdToken() {
        return this.delegate.getIdToken();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.delegate.getAuthorities();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.delegate.getAttributes();
    }

    @Override
    public String getName() {
        return this.delegate.getName();
    }

    @Override
    public String toString() {
        return this.delegate.toString();
    }

    public String getGraphApiToken() {
        return this.graphApiToken;
    }
}

