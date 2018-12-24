/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.b2c;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * AAD B2C filter scenario handler will handle different scenario when redirect from AAD B2C back to
 * spring security. The scenario include but not limited to:
 * <p>
 * ${@link AADB2CFilterDefaultHandler} is default handler for AAD B2C filter, do nothing at all.
 * <p>
 * ${@link AADB2CFilterPolicyReplyHandler} will obtain the result from AAD B2C, it will generate UserPrinciple
 * and ${@link org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken} for
 * spring security context if success, or take care the error response and throw exception.
 * <p>
 * ${@link AADB2CFilterPasswordResetHandler} will take care of the password reset url and redirect to AAD B2C for
 * processing password resetting.
 */
public interface AADB2CFilterScenarioHandler {
    /**
     * Handle different scenario for AAD B2C filter.
     *
     * @param request  from
     *                 ${@link AADB2CFilter#doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain)}
     * @param response from
     *                 ${@link AADB2CFilter#doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain)}
     * @throws ServletException when authentication fails.
     * @throws IOException      when redirection fails.
     */
    void handle(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException, AADB2CAuthenticationException;

    /**
     * Check if ${@link HttpServletRequest} matches filter scenario, if matches the handler will take action.
     *
     * @param request from ${@link AADB2CFilter}.
     * @return true if scenario matches, or false.
     */
    Boolean matches(HttpServletRequest request);

    AADB2CFilterScenarioHandler getSuccessor();

    void setSuccessor(AADB2CFilterScenarioHandler handler);
}
