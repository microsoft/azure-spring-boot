/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.b2c;

import lombok.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;

public class AADB2CFilter extends OncePerRequestFilter {

    private final AADB2CFilterPolicyReplyHandler policyReplyHandler;

    private final AADB2CFilterPasswordResetHandler passwordResetHandler;

    private final AADB2CFilterProfileEditHandler profileEditHandler;

    private final AADB2CFilterForgotPasswordHandler forgotPasswordHandler;

    private final AADB2CFilterDefaultHandler defaultHandler = new AADB2CFilterDefaultHandler();

    public AADB2CFilter(@NonNull AADB2CFilterPolicyReplyHandler policyReplyHandler,
                        @Nullable AADB2CFilterPasswordResetHandler passwordResetHandler,
                        @Nullable AADB2CFilterProfileEditHandler profileEditHandler,
                        @Nullable AADB2CFilterForgotPasswordHandler forgotPasswordHandler) {
        super();

        this.policyReplyHandler = policyReplyHandler;
        this.passwordResetHandler = passwordResetHandler;
        this.profileEditHandler = profileEditHandler;
        this.forgotPasswordHandler = forgotPasswordHandler;
    }

    @Override
    public void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
                                 @NotNull FilterChain chain) throws IOException, ServletException {
        try {
            if (forgotPasswordHandler != null && forgotPasswordHandler.matches(request)) {
                forgotPasswordHandler.handle(request, response, chain);
            } else if (policyReplyHandler.matches(request)) {
                policyReplyHandler.handle(request, response, chain);
            } else if (passwordResetHandler != null && passwordResetHandler.matches(request)) {
                passwordResetHandler.handle(request, response, chain);
            } else if (profileEditHandler != null && profileEditHandler.matches(request)) {
                profileEditHandler.handle(request, response, chain);
            } else {
                defaultHandler.handle(request, response, chain);
            }
        } catch (AADB2CAuthenticationException e) {
            throw new ServletException("Authentication failed", e);
        }
    }
}
