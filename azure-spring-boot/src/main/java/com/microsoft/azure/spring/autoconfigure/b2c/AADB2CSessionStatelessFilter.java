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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;

import static com.microsoft.azure.spring.autoconfigure.b2c.AADB2CURL.*;

@Slf4j
public class AADB2CSessionStatelessFilter extends OncePerRequestFilter {

    private final AADB2CJWTProcessor aadb2CJWTProcessor;

    private static final String AUTH_TYPE = "Bearer";

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    public AADB2CSessionStatelessFilter(@NonNull AADB2CProperties b2cProperties) {
        final String openIdConfigURL = AADB2CURL.getOpenIdSignUpOrInConfigurationURL(b2cProperties);
        this.aadb2CJWTProcessor = new AADB2CJWTProcessor(openIdConfigURL, b2cProperties);
    }

    private void updateSecurityContext(@NonNull UserPrincipal principal) {
        final Authentication auth = new PreAuthenticatedAuthenticationToken(principal, null);
        auth.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(auth);

        log.debug("User {} is authenticated.", principal.getDisplayName());
    }

    private void doReplyIdTokenFilter(HttpServletRequest request, HttpServletResponse response)
            throws AADB2CAuthenticationException, IOException {
        final String idToken = request.getParameter(PARAMETER_ID_TOKEN);
        final String code = request.getParameter(PARAMETER_CODE);
        final Pair<JWSObject, JWTClaimsSet> jwtToken = aadb2CJWTProcessor.validate(idToken);
        final UserPrincipal principal = new UserPrincipal(jwtToken, code);

        response.setStatus(HttpStatus.OK.value());
        response.addCookie(new Cookie(PARAMETER_ID_TOKEN, idToken));

        updateSecurityContext(principal);
        redirectStrategy.sendRedirect(request, response, request.getRequestURL().toString());

        log.debug("Redirecting to {}.", principal.getDisplayName(), request.getRequestURL().toString());
    }

    private void doAuthenticationFilter(HttpServletRequest request) throws AADB2CAuthenticationException {
        final String authHeader = request.getHeader("Authentication");
        Assert.isTrue(authHeader.startsWith(AUTH_TYPE), "Unexpected authentication type: " + authHeader);

        final String idToken = authHeader.substring(AUTH_TYPE.length() + 1);
        final Pair<JWSObject, JWTClaimsSet> jwtToken = aadb2CJWTProcessor.validate(idToken);
        final UserPrincipal principal = new UserPrincipal(jwtToken, null);

        updateSecurityContext(principal);
    }

    @Override
    public void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
                                 @NotNull FilterChain chain) throws IOException, ServletException {
        try {
            AADB2CURL.validateReplyRequest(request);

            if (StringUtils.hasText(request.getParameter(PARAMETER_ID_TOKEN))) {
                doReplyIdTokenFilter(request, response);
            } else if (StringUtils.hasText(request.getHeader(HEADER_AUTHENTICATION))) {
                doAuthenticationFilter(request);
            }
        } catch (AADB2CAuthenticationException e) {
            throw new ServletException("Validate reply url reqest failure.", e);
        }

        chain.doFilter(request, response);
        SecurityContextHolder.getContext().setAuthentication(null);
    }
}
