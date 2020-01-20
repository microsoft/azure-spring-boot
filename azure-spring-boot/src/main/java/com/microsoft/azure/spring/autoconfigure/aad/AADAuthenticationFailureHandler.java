/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.aad;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.microsoft.aad.adal4j.AdalClaimsChallengeException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;

public class AADAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private AuthenticationFailureHandler defaultHandler;

    public AADAuthenticationFailureHandler() {
        this.defaultHandler = new SimpleUrlAuthenticationFailureHandler(AADConstants.FAILURE_DEFUALT_URL);
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        final OAuth2AuthenticationException targetException = (OAuth2AuthenticationException) exception;
        //handle conditional access policy
        if (AADConstants.CONDITIONAL_ACCESS_POLICY.equals((targetException.getError().getErrorCode()))) {
            //get infos
            final Throwable cause = targetException.getCause();
            if (cause instanceof AdalClaimsChallengeException) {
                final AdalClaimsChallengeException acce = (AdalClaimsChallengeException) cause;
                final String claims = acce.getClaims();

                final DefaultSavedRequest savedRequest = (DefaultSavedRequest) request.getSession()
                        .getAttribute(AADConstants.SAVED_REQUEST);
                final String savedRequestUrl = savedRequest.getRedirectUrl();
                //put claims into session
                request.getSession().setAttribute(AADConstants.CAP_CLAIMS, claims);
                //redirect
                response.setStatus(302);
                response.sendRedirect(savedRequestUrl);
                return;
            }
        }
        //default handle logic
        defaultHandler.onAuthenticationFailure(request, response, exception);
    }
}
