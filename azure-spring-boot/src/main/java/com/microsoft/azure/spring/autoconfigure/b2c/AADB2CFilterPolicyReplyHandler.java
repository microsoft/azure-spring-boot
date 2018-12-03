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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public class AADB2CFilterPolicyReplyHandler extends AbstractAADB2CFilterScenarioHandler
        implements AADB2CFilterScenarioHandler {

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
//        Assert.hasText(state, "state should contains text.");
//
//        final String requestURL = request.getSession().getAttribute(AADB2CURL.ATTRIBUTE_STATE).toString();
//
//        if (!state.equals(requestURL)) {
//            throw new AADB2CAuthenticationException("The reply state has unexpected content: " + state);
//        }
    }

    private void validateNonce(String nonce, HttpServletRequest request) throws AADB2CAuthenticationException {
//        Assert.hasText(nonce, "nonce should contains text.");
//
//        final String expectedNonce = request.getSession().getAttribute(AADB2CURL.ATTRIBUTE_NONCE).toString();
//
//        if (!nonce.equals(expectedNonce)) {
//            throw new AADB2CAuthenticationException("The claim nonce has unexpected content: " + nonce);
//        }
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

        final Authentication auth = new PreAuthenticatedAuthenticationToken(principal, null);
        auth.setAuthenticated(true);

        SecurityContextHolder.getContext().setAuthentication(auth);
        redirectStrategy.sendRedirect(request, response, state);

        log.debug("User {} is authenticated. Redirecting to {}.", principal.getDisplayName(), state);
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws AADB2CAuthenticationException, IOException, ServletException {
        super.validatePolicyReply(request);
        this.handlePolicyReplyAuthentication(request, response);

        chain.doFilter(request, response);
    }

    private boolean isPolicyReplyURL(@URL String requestURL) {
        final String signUpOrInRedirectURL = b2cProperties.getPolicies().getSignUpOrSignIn().getReplyURL();
        final AADB2CProperties.Policy passwordReset = b2cProperties.getPolicies().getPasswordReset();
        final AADB2CProperties.Policy profileEdit = b2cProperties.getPolicies().getProfileEdit();

        if (requestURL.equals(signUpOrInRedirectURL)) {
            return true;
        } else if (passwordReset != null && requestURL.equals(passwordReset.getReplyURL())) {
            return true;
        } else if (profileEdit != null && requestURL.equals(profileEdit.getReplyURL())) {
            return true;
        } else {
            return false;
        }
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
            return isPolicyReplyURL(requestURL);
        }
    }
}
