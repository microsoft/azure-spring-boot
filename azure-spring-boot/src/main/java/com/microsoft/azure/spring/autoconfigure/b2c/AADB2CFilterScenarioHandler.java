/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.b2c;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * AAD B2C filter scenario handler will handle different scenario when redirect from AAD B2C back to
 * spring security. The scenario include but not limited to:
 * <p>
 * ${@link AADB2CFilterDefaultHandler} is default handler for AAD B2C filter, do nothing at all.
 * <p>
 * ${@link AADB2CFilterSignUpOrInHandler} will obtain the result from AAD B2C, it will generate UserPrinciple
 * and ${@link org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken} for
 * spring security context if success, or take care the error response and throw exception.
 * <p>
 * ${@link AADB2CFilterLogoutSuccessHandler} will obtain the result from AAD B2C, it will do nothing if success,
 * or warn the user that the token failed to expireDate.
 */
public interface AADB2CFilterScenarioHandler {
    /**
     * Handle different scenario for AAD B2C filter.
     *
     * @param request       from
     *                      ${@link AADB2CFilter#doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain)}
     * @param response      from
     *                      ${@link AADB2CFilter#doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain)}
     * @param b2cProperties of ${@link AADB2CProperties} represents customer configuration.
     * @throws AADB2CAuthenticationException when authentication fails.
     * @throws IOException                   when redirection fails.
     */
    void handle(HttpServletRequest request, HttpServletResponse response, AADB2CProperties b2cProperties)
            throws AADB2CAuthenticationException, IOException;
}
