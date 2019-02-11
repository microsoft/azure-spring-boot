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
import org.springframework.util.StringUtils;

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

    private final String passwordResetPolicyValue;

    public AADB2CAuthorizationRequestResolver(@NonNull ClientRegistrationRepository repository) {
        this.passwordResetPolicyValue = null;
        this.defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(repository, REQUEST_BASE_URI);
    }

    public AADB2CAuthorizationRequestResolver(@NonNull ClientRegistrationRepository repository,
                                              @Nullable String passwordResetPolicyValue) {
        this.passwordResetPolicyValue = passwordResetPolicyValue;
        this.defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(repository, REQUEST_BASE_URI);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(@NonNull HttpServletRequest request) {
        return resolve(request, getRegistrationId(request));
    }

    @Override
    public OAuth2AuthorizationRequest resolve(@NonNull HttpServletRequest request, String registrationId) {
        if (StringUtils.hasText(passwordResetPolicyValue) && isForgotPasswordAuthorizationRequest(request)) {
            final OAuth2AuthorizationRequest authRequest = defaultResolver.resolve(request, passwordResetPolicyValue);
            return getB2CAuthorizationRequest(authRequest, passwordResetPolicyValue);
        }

        if (StringUtils.hasText(registrationId) && requestMatcher.matches(request)) {
            return getB2CAuthorizationRequest(defaultResolver.resolve(request), registrationId);
        }

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

    private String getRegistrationId(HttpServletRequest request) {
        if (requestMatcher.matches(request)) {
            return requestMatcher.extractUriTemplateVariables(request).get(REGISTRATION_ID_NAME);
        }

        return null;
    }

    // Handle the forgot password of sign-up-or-in page cannot redirect user to password-reset page.
    // The B2C service will enhance that, and then related code will be removed.
    private boolean isForgotPasswordAuthorizationRequest(@NonNull HttpServletRequest request) {
        final String error = request.getParameter("error");
        final String description = request.getParameter("error_description");

        if ("access_denied".equals(error)) {
            Assert.hasText(description, "description should contain text.");
            return description.startsWith("AADB2C90118:");
        }

        return false;
    }
}
