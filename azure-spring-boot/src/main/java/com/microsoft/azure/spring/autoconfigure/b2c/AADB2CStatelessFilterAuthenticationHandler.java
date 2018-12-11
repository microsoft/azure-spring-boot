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
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class AADB2CStatelessFilterAuthenticationHandler extends AbstractAADB2CFilterScenarioHandler
        implements AADB2CFilterScenarioHandler {

    private static final String AUTH_TYPE = "Bearer";

    private static final String AUTH_HEADER = "Authentication";

    private final AADB2CJWTProcessor jwtProcessor;

    public AADB2CStatelessFilterAuthenticationHandler(@NonNull AADB2CProperties b2cProperties) {
        final String openIdConfigURL = AADB2CURL.getOpenIdSignUpOrInConfigurationURL(b2cProperties);

        this.jwtProcessor = new AADB2CJWTProcessor(openIdConfigURL, b2cProperties);
    }

    private void handleAuthenticationHeader(HttpServletRequest request, HttpServletResponse response)
            throws AADB2CAuthenticationException {
        final String authHeader = request.getHeader(AUTH_HEADER);
        Assert.isTrue(authHeader.startsWith(AUTH_TYPE), "Unexpected authentication type: " + authHeader);

        final String idToken = authHeader.substring(AUTH_TYPE.length() + 1);
        final Pair<JWSObject, JWTClaimsSet> jwtToken = jwtProcessor.validate(idToken);
        final UserPrincipal principal = new UserPrincipal(jwtToken, null);

        if (!principal.isUserValid() || principal.isUserExpired()) {
            response.addCookie(new Cookie(AADB2CURL.PARAMETER_ID_TOKEN, ""));
        } else {
            super.updateSecurityContext(principal);
        }
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws AADB2CAuthenticationException {
        AADB2CURL.validateReplyRequest(request);
        handleAuthenticationHeader(request, response);
    }

    @Override
    public Boolean matches(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authentication");

        return StringUtils.hasText(authHeader) && authHeader.startsWith(AUTH_TYPE);
    }
}
