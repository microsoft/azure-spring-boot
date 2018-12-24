/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.b2c;

import com.nimbusds.jose.JWSObject;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.microsoft.azure.spring.autoconfigure.b2c.AADB2CURL.*;

@Slf4j
public class AADB2CStatelessPolicyReplyHandler extends AbstractAADB2CFilterScenarioHandler {

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    private final AADB2CProperties b2cProperties;

    private final AADB2CJWTProcessor jwtProcessor;

    public AADB2CStatelessPolicyReplyHandler(@NonNull AADB2CProperties b2cProperties) {
        final String openIdConfigURL = AADB2CURL.getOpenIdSignUpOrInConfigurationURL(b2cProperties);

        this.b2cProperties = b2cProperties;
        this.jwtProcessor = new AADB2CJWTProcessor(openIdConfigURL, b2cProperties);
    }

    private void handlePolicyReplyIdToken(HttpServletRequest request, HttpServletResponse response)
            throws AADB2CAuthenticationException, IOException {
        final String idToken = request.getParameter(PARAMETER_ID_TOKEN);
        final String code = request.getParameter(PARAMETER_CODE);

        Assert.hasText(idToken, "idToken should contain text.");
        Assert.hasText(code, "code should contain text.");

        final Pair<JWSObject, JWTClaimsSet> jwtToken = jwtProcessor.validate(idToken);
        final UserPrincipal principal = new UserPrincipal(jwtToken, code);

        response.setStatus(HttpStatus.OK.value());
        response.addCookie(new Cookie(PARAMETER_ID_TOKEN, idToken));

        super.updateSecurityContext(principal);
        redirectStrategy.sendRedirect(request, response, request.getRequestURL().toString());

        log.debug("Redirecting to {}.", principal.getDisplayName(), request.getRequestURL().toString());
    }

    @Override
    public void handleInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws AADB2CAuthenticationException, IOException, ServletException {
        AADB2CURL.validateReplyRequest(request);
        handlePolicyReplyIdToken(request, response);

        chain.doFilter(request, response);
    }

    @Override
    public Boolean matches(HttpServletRequest request) {
        final String requestURL = request.getRequestURL().toString();
        final String idToken = request.getParameter(PARAMETER_ID_TOKEN);
        final String error = request.getParameter(PARAMETER_ERROR);

        if (!StringUtils.hasText(idToken) && !StringUtils.hasText(error)) {
            return false;
        } else {
            return super.isPolicyReplyURL(requestURL, b2cProperties);
        }
    }
}
