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
import org.hibernate.validator.constraints.URL;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.microsoft.azure.spring.autoconfigure.b2c.AADB2CURL.*;

@Slf4j
public class AADB2CFilterPolicyReplyHandler extends AbstractAADB2CFilterScenarioHandler {

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    private final AADB2CProperties b2cProperties;

    /**
     * Mapping configuration URL to ${@link AADB2CJWTProcessor}.
     */
    private static final ConcurrentMap<String, AADB2CJWTProcessor> urlToJWTParser = new ConcurrentHashMap<>();

    public AADB2CFilterPolicyReplyHandler(@NonNull AADB2CProperties b2cProperties) {
        this.b2cProperties = b2cProperties;
    }

    /**
     * Get cached instance of ${@link AADB2CJWTProcessor} from given url, or create new one.
     *
     * @param url        of the configuration.
     * @param properties of ${@link AADB2CProperties}.
     * @return the instance of ${@link AADB2CJWTProcessor}.
     */
    private AADB2CJWTProcessor getAADB2CJwtProcessor(@URL String url, @NonNull AADB2CProperties properties) {
        return urlToJWTParser.computeIfAbsent(url, k -> new AADB2CJWTProcessor(k, properties));
    }

    private void validateState(String state, HttpServletRequest request) throws AADB2CAuthenticationException {
        Assert.hasText(state, "state should contains text.");

        final String sessionState = request.getSession().getAttribute(AADB2CURL.ATTRIBUTE_STATE).toString();

        if (!state.equals(sessionState)) {
            throw new AADB2CAuthenticationException("The reply state has unexpected content: " + state);
        }
    }

    private void validateNonce(String nonce, HttpServletRequest request) throws AADB2CAuthenticationException {
        Assert.hasText(nonce, "nonce should contains text.");

        final String expectedNonce = request.getSession().getAttribute(AADB2CURL.ATTRIBUTE_NONCE).toString();

        if (!nonce.equals(expectedNonce)) {
            throw new AADB2CAuthenticationException("The claim nonce has unexpected content: " + nonce);
        }
    }

    private void handlePolicyReplyAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AADB2CAuthenticationException, IOException {
        final String idToken = request.getParameter(PARAMETER_ID_TOKEN);
        final String code = request.getParameter(PARAMETER_CODE);

        Assert.hasText(idToken, "idToken should contain text.");
        Assert.hasText(code, "code should contain text.");

        final String url = AADB2CURL.getOpenIdSignUpOrInConfigurationURL(b2cProperties);
        final Pair<JWSObject, JWTClaimsSet> jwtToken = getAADB2CJwtProcessor(url, b2cProperties).validate(idToken);
        final UserPrincipal principal = new UserPrincipal(jwtToken, code);
        final String state = request.getParameter(PARAMETER_STATE);

        validateState(state, request);
        validateNonce(principal.getNonce(), request);
        super.updateSecurityContext(principal);

        final String requestURL = AADB2CURL.getStateRequestUrl(state);
        redirectStrategy.sendRedirect(request, response, requestURL);

        log.debug("Redirecting to {}.", principal.getDisplayName(), requestURL);
    }

    @Override
    public void handleInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws AADB2CAuthenticationException, IOException, ServletException {
        AADB2CURL.validateReplyRequest(request);
        handlePolicyReplyAuthentication(request, response);

        chain.doFilter(request, response);
    }

    @Override
    public Boolean matches(HttpServletRequest request) {
        final String requestURL = request.getRequestURL().toString();
        final String idToken = request.getParameter(PARAMETER_ID_TOKEN);
        final String error = request.getParameter(PARAMETER_ERROR);

        if (!StringUtils.hasText(idToken) && !StringUtils.hasText(error)) {
            return false;
        } else if (!HttpMethod.GET.matches(request.getMethod())) {
            return false;
        } else {
            return super.isPolicyReplyURL(requestURL, b2cProperties);
        }
    }
}
