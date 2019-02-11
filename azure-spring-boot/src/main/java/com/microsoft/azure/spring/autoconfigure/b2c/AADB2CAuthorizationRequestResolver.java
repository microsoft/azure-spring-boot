/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.b2c;

import lombok.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class AADB2CAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private static final String REQUEST_BASE_URI =
            OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI;

    private static final String REGISTRATION_ID_NAME = "registrationId";

    private static final String MATCHER_PATTERN = String.format("%s/{%s}", REQUEST_BASE_URI, REGISTRATION_ID_NAME);

    private static final AntPathRequestMatcher requestMatcher = new AntPathRequestMatcher(MATCHER_PATTERN);

    private final OAuth2AuthorizationRequestResolver defaultResolver;

    public AADB2CAuthorizationRequestResolver(@NonNull ClientRegistrationRepository repository) {
        this.defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(repository, REQUEST_BASE_URI);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(@NonNull HttpServletRequest request) {
        if (requestMatcher.matches(request)) {
            return getB2CAuthorizationRequest(defaultResolver.resolve(request), getRegistrationId(request));
        }

        // Return null may not be the good practice, but we need to align with oauth2.client.web
        // DefaultOAuth2AuthorizationRequestResolver.
        return null;
    }

    private void cleanupSecurityContextAuthentication() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    private OAuth2AuthorizationRequest getB2CAuthorizationRequest(@Nullable OAuth2AuthorizationRequest request,
                                                                  String policyValue) {
        Assert.hasText(policyValue, "Policy value should contain text.");

        if (request == null) {
            return null;
        }

        cleanupSecurityContextAuthentication();

        final Map<String, Object> parameters = new HashMap<>(request.getAdditionalParameters());

        parameters.put("p", policyValue);

        return OAuth2AuthorizationRequest.from(request).additionalParameters(parameters).build();
    }

    @Override
    public OAuth2AuthorizationRequest resolve(@NonNull HttpServletRequest request, String registrationId) {
        Assert.hasText(registrationId, "registrationId should have text.");

        if (requestMatcher.matches(request)) {
            return getB2CAuthorizationRequest(defaultResolver.resolve(request), registrationId);
        }

        return null;
    }

    private String getRegistrationId(HttpServletRequest request) {
        if (requestMatcher.matches(request)) {
            return requestMatcher.extractUriTemplateVariables(request).get(REGISTRATION_ID_NAME);
        }

        return null;
    }
}
