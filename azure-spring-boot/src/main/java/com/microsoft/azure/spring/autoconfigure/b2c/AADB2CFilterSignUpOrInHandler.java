/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.b2c;

import com.nimbusds.jose.JWSObject;
import com.nimbusds.jwt.JWTClaimsSet;
import io.jsonwebtoken.lang.Assert;
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
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
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
        // TODO(pan): url should exclude state and nonce UUID part.
        URL_TO_JWT_PARSER.putIfAbsent(url, new AADB2CJWTProcessor(url, properties));

        return URL_TO_JWT_PARSER.get(url);
    }

    /**
     * Validate the reply state from AAD B2C, the state compose of tow parts with format 'UUID-RequestURL',
     * for example: 461e6d45-37cf-4a8f-9fd8-086b98c8abfb-http://localhost:8080/
     *
     * @param state encoded in policy URL and replied by AAD B2C.
     * @return the request URL.
     */
    private String validateState(String state) throws AADB2CAuthenticationException {
        final int uuidLength = UUID.randomUUID().toString().length();

        Assert.hasText(state, "state should contains text.");
        Assert.isTrue(state.length() > uuidLength, "");

        final String replyUUID = state.substring(0, uuidLength);
        final String requestURL = state.substring(uuidLength + 1);

        log.debug("Decode state to UUID {}, request URL {}.", replyUUID, requestURL);

        if (!AADB2CURL.isValidState(replyUUID)) {
            throw new AADB2CAuthenticationException("Invalid UUID from reply state.");
        }

        return requestURL;
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
            final String requestURL = validateState(request.getParameter(PARAMETER_STATE));
            final String url = AADB2CURL.getOpenIdSignUpOrInConfigurationURL(properties);
            final Pair<JWSObject, JWTClaimsSet> jwtToken = getAADB2CJwtProcessor(url, properties).validate(idToken);
            final UserPrincipal principal = new UserPrincipal(jwtToken, code);

            final Authentication auth = new PreAuthenticatedAuthenticationToken(principal, null);
            auth.setAuthenticated(true);

            SecurityContextHolder.getContext().setAuthentication(auth);

            redirectStrategy.sendRedirect(request, response, requestURL);
            log.debug("Authenticated user {}, will redirect to {}.", principal.getDisplayName(), requestURL);
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
            log.debug("User principal {} not authenticated.", ((UserPrincipal) auth.getPrincipal()).getDisplayName());
            ((PreAuthenticatedAuthenticationToken) auth).setAuthenticated(false);
        }

        super.validateAuthentication(request);
        this.handleAuthentication(request, response, properties);
    }
}
