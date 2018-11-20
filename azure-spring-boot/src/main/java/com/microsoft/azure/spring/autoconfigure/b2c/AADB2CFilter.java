/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.b2c;

import lombok.Getter;
import lombok.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AADB2CFilter extends OncePerRequestFilter {

    @Getter
    private final String signUpOrInRedirectURL;

    private AADB2CLogoutSuccessHandler logoutSuccessHandler;

    public AADB2CFilter(@NonNull AADB2CProperties b2cProperties,
                        @NonNull AADB2CLogoutSuccessHandler logoutSuccessHandler) {
        super();

        this.logoutSuccessHandler = logoutSuccessHandler;
        this.signUpOrInRedirectURL = b2cProperties.getPolicies().getSignUpOrSignIn().getRedirectURI();
    }

    public String getLogoutSuccessURL() {
        return this.logoutSuccessHandler.getLogoutSuccessURL();
    }

    @Override
    public void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                 @NonNull FilterChain chain) throws IOException, ServletException {
        AADB2CFilterScenario.resolve(request, this).getScenarioHandler().handle(request, response);
        chain.doFilter(request, response);
    }
}
