/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.b2c;

import com.nimbusds.jose.JWSObject;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.validator.constraints.URL;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@NoArgsConstructor
public class AADB2CFilterSignUpOrInHandler extends AbstractAADB2CFilterScenarioHandler
        implements AADB2CFilterScenarioHandler {

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    /**
     * Mapping configuration URL to ${@link AADB2CJWTProcessor}.
     */
    private static final ConcurrentMap<String, AADB2CJWTProcessor> URL_TO_JWT_PARSER = new ConcurrentHashMap<>();

    /**
     * Get cached instance of ${@link AADB2CJWTProcessor} from given url, or create new one.
     *
     * @param url        of the configuration.
     * @param properties of ${@link AADB2CProperties}.
     * @return the instance of ${@link AADB2CJWTProcessor}.
     */
    private AADB2CJWTProcessor getAADB2CJwtProcessor(@URL String url, @NonNull AADB2CProperties properties) {
        return URL_TO_JWT_PARSER.computeIfAbsent(url, k -> new AADB2CJWTProcessor(k, properties));
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

    private boolean isAuthenticated(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        } else if (auth instanceof PreAuthenticatedAuthenticationToken) {
            final UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
            return !principal.isUserExpired() && principal.isUserValid();
        } else {
            return auth.isAuthenticated();
        }
    }

    private void handleAuthentication(HttpServletRequest request, HttpServletResponse response,
                                      AADB2CProperties properties) throws AADB2CAuthenticationException, IOException {
        final String idToken = request.getParameter(PARAMETER_ID_TOKEN);
        final String code = request.getParameter(PARAMETER_CODE);

        if (StringUtils.hasText(idToken) && StringUtils.hasText(code)) {
            final String url = AADB2CURL.getOpenIdSignUpOrInConfigurationURL(properties);
            final Pair<JWSObject, JWTClaimsSet> jwtToken = getAADB2CJwtProcessor(url, properties).validate(idToken);
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
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AADB2CProperties properties)
            throws AADB2CAuthenticationException, IOException {
        Assert.notNull(properties, "AADB2CProperties should not be null.");

        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (isAuthenticated(auth)) {
            return;
        } else if (auth instanceof PreAuthenticatedAuthenticationToken) {
            log.debug("User {} is not authenticated.", ((UserPrincipal) auth.getPrincipal()).getDisplayName());
            ((PreAuthenticatedAuthenticationToken) auth).setAuthenticated(false);
        }

        super.validateAuthentication(request);
        this.handleAuthentication(request, response, properties);
    }
}
