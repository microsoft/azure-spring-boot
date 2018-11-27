/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.b2c;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class AADB2CLogoutSuccessHandler implements LogoutSuccessHandler {

    private String logoutSuccessURL;

    private final AADB2CProperties b2cProperties;

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    public AADB2CLogoutSuccessHandler(@NonNull AADB2CProperties b2cProperties) {
        this.b2cProperties = b2cProperties;
    }

    /**
     * Get the logout success URL.
     *
     * @return the URL of logout success.
     */
    public String getLogoutSuccessURL() {
        if (logoutSuccessURL != null) {
            return logoutSuccessURL;
        } else {
            return b2cProperties.getPolicies().getSignUpOrSignIn().getRedirectURI();
        }
    }

    public AADB2CLogoutSuccessHandler with(String logoutSuccessURL) {
        this.logoutSuccessURL = logoutSuccessURL;

        return this;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        final String redirectURL = AADB2CURL.getOpenIdLogoutURL(b2cProperties, getLogoutSuccessURL(), request);

        log.debug("Redirect to AAD B2C URL {} for expiring token.", redirectURL);
        log.debug("AAD B2C token has been successfully expired. Redirecting to URL {}.", getLogoutSuccessURL());

        redirectStrategy.sendRedirect(request, response, redirectURL);
    }
}
