/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.b2c;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;

@Slf4j
public class AADB2CStatelessFilter extends OncePerRequestFilter {

    private final AADB2CFilterScenarioHandlerChain handlerChain;

    public AADB2CStatelessFilter(@NonNull AADB2CFilterScenarioHandlerChain handlerChain) {
        super();

        this.handlerChain = handlerChain;
    }

    @Override
    public void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
                                 @NotNull FilterChain chain) throws IOException, ServletException {
        try {
            handlerChain.handle(request, response, chain);
        } catch (AADB2CAuthenticationException e) {
            throw new ServletException("Validate reply url failure.", e);
        }

        SecurityContextHolder.getContext().setAuthentication(null);
    }
}
