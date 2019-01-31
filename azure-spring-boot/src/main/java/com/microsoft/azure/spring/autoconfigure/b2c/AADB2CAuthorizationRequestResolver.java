/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.b2c;

import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class AADB2CAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private final AADB2CProperties properties;

    private final OAuth2AuthorizationRequestResolver defaultResolver;

    public AADB2CAuthorizationRequestResolver(ClientRegistrationRepository registrationRepository,
                                              AADB2CProperties properties) {
        this.defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(registrationRepository,
                "/oauth2/authorization");
        // TODO(pan): Investigate why should be below string.
        this.properties = properties;
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        Assert.notNull(request, "request cannot be null.");

        return getAADB2CAuthorizationRequest(defaultResolver.resolve(request));
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String registrationId) {
        Assert.notNull(request, "request cannot be null.");
        Assert.hasText(registrationId, "registrationId should have text.");

        return getAADB2CAuthorizationRequest(defaultResolver.resolve(request, registrationId));
    }

    private OAuth2AuthorizationRequest getAADB2CAuthorizationRequest(OAuth2AuthorizationRequest request) {
        if (request == null) {
            return null;
        }

        final Map<String, Object> parameters = new HashMap<>(request.getAdditionalParameters());

        parameters.putIfAbsent("p", properties.getPolicies().getSignUpOrSignIn().getName());

        return OAuth2AuthorizationRequest.from(request).additionalParameters(parameters).build();
    }
}
