/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.b2c;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class AADB2CFilterProfileEditHandler extends AbstractAADB2CFilterScenarioHandler {

    private final AADB2CProperties b2cProperties;

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    public AADB2CFilterProfileEditHandler(@NonNull AADB2CProperties b2cProperties) {
        this.b2cProperties = b2cProperties;
    }

    private String getRefererURL(HttpServletRequest request) {
        final String refererURL = request.getHeader("Referer");

        if (StringUtils.hasText(refererURL)) {
            return refererURL;
        }

        return b2cProperties.getPolicies().getProfileEdit().getReplyURL();
    }

    @Override
    public void handleInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws AADB2CAuthenticationException, IOException {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (super.isAuthenticated(auth)) {
            final String refererURL = getRefererURL(request);
            final String url = AADB2CURL.getOpenIdProfileEditURL(b2cProperties, refererURL, request);

            redirectStrategy.sendRedirect(request, response, url);

            log.debug("Redirect authenticated user to profile edit URL: {}.", url);
        } else {
            throw new AADB2CAuthenticationException("Authentication is required for profile editing.");
        }
    }

    @Override
    public Boolean matches(HttpServletRequest request) {
        final String requestURL = request.getRequestURL().toString();
        final String profileEditURL = b2cProperties.getProfileEditUrl();

        return HttpMethod.GET.matches(request.getMethod()) && requestURL.equals(profileEditURL);
    }
}
