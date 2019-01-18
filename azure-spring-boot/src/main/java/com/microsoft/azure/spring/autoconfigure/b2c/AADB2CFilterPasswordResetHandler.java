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
public class AADB2CFilterPasswordResetHandler extends AbstractAADB2CFilterScenarioHandler {

    private final AADB2CProperties b2cProperties;

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    public AADB2CFilterPasswordResetHandler(@NonNull AADB2CProperties b2cProperties) {
        this.b2cProperties = b2cProperties;
    }

    private String getRefererURL(HttpServletRequest request) {
        final String refererURL = request.getHeader("Referer");

        if (StringUtils.hasText(refererURL)) {
            return refererURL;
        }

        return b2cProperties.getPolicies().getPasswordReset().getReplyURL();
    }

    @Override
    public void handleInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws AADB2CAuthenticationException, IOException {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (super.isAuthenticated(auth)) {
            final String refererURL = getRefererURL(request);
            final String url = AADB2CURL.getOpenIdPasswordResetURL(b2cProperties, refererURL, request);

            if (auth != null) {
                auth.setAuthenticated(false);
            }

            redirectStrategy.sendRedirect(request, response, url);
            log.debug("Redirect authenticated user to password reset URL: {}.", url);
        } else {
            throw new AADB2CAuthenticationException("Authentication is required when password reset.");
        }
    }

    @Override
    public Boolean matches(HttpServletRequest request) {
        final String requestURL = request.getRequestURL().toString();
        final String passwordResetURL = b2cProperties.getPasswordResetUrl();

        return HttpMethod.GET.matches(request.getMethod()) && requestURL.equals(passwordResetURL);
    }
}
