/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.b2c;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.URL;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public abstract class AbstractAADB2CFilterScenarioHandler implements AADB2CFilterScenarioHandler {

    @Getter
    @Setter
    protected AADB2CFilterScenarioHandler successor = null;

    protected boolean isAuthenticated(Authentication auth) {
        if (auth == null) {
            return false;
        } else if (auth instanceof PreAuthenticatedAuthenticationToken) {
            final UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
            return !principal.isUserExpired() && principal.isUserValid();
        } else {
            return auth.isAuthenticated();
        }
    }

    protected void updateAuthentication() {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null) {
            auth.setAuthenticated(isAuthenticated(auth));
        }
    }

    protected boolean isPolicyReplyURL(@URL String requestURL, @NonNull AADB2CProperties b2cProperties) {
        final String signUpOrInRedirectURL = b2cProperties.getPolicies().getSignUpOrSignIn().getReplyURL();
        final AADB2CProperties.Policy passwordReset = b2cProperties.getPolicies().getPasswordReset();
        final AADB2CProperties.Policy profileEdit = b2cProperties.getPolicies().getProfileEdit();

        if (requestURL.equals(signUpOrInRedirectURL)) {
            return true;
        } else if (passwordReset != null && requestURL.equals(passwordReset.getReplyURL())) {
            return true;
        } else if (profileEdit != null && requestURL.equals(profileEdit.getReplyURL())) {
            return true;
        } else {
            return false;
        }
    }

    protected void updateSecurityContext(@NonNull UserPrincipal principal) {
        final Authentication auth = new PreAuthenticatedAuthenticationToken(principal, null);
        auth.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(auth);

        log.debug("User {} is authenticated.", principal.getDisplayName());
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws AADB2CAuthenticationException, IOException, ServletException {
        if (matches(request)) {
            handleInternal(request, response, chain);
        } else if (successor != null) {
            successor.handle(request, response, chain);
        }
    }

    protected abstract void handleInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws AADB2CAuthenticationException, IOException, ServletException;
}
