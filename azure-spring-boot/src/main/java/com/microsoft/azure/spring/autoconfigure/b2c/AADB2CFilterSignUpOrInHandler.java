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
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@NoArgsConstructor
public class AADB2CFilterSignUpOrInHandler extends AbstractAADB2CFilterScenarioHandler
        implements AADB2CFilterScenarioHandler {

    /**
     * Mapping configuration URL to ${@link AADB2CJWTProcessor}.
     */
    private final ConcurrentMap<String, AADB2CJWTProcessor> urlToJwtParser = new ConcurrentHashMap<>();

    /**
     * Get cached instance of ${@link AADB2CJWTProcessor} from given url, or create new one.
     *
     * @param url        of the configuration.
     * @param properties of ${@link AADB2CProperties}.
     * @return the instance of ${@link AADB2CJWTProcessor}.
     * @throws AADB2CAuthenticationException when failed to create ${@link AADB2CJWTProcessor} instance.
     */
    private AADB2CJWTProcessor getAADB2CJwtParser(@URL String url, @NonNull AADB2CProperties properties)
            throws AADB2CAuthenticationException {
        urlToJwtParser.putIfAbsent(url, new AADB2CJWTProcessor(url, properties));

        return urlToJwtParser.get(url);
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AADB2CProperties properties)
            throws AADB2CAuthenticationException {
        super.validateAuthentication(request);

        Assert.notNull(properties, "AADB2CProperties should not be null.");

        final String idToken = request.getParameter(PARAMETER_ID_TOKEN);
        final String code = request.getParameter(PARAMETER_CODE);

        if (StringUtils.hasText(idToken) && StringUtils.hasText(code)) {
            final String url = AADB2CURL.getOpenIdSignUpOrInConfigurationURL(properties);
            final AADB2CJWTProcessor processor = getAADB2CJwtParser(url, properties);
            final Pair<JWSObject, JWTClaimsSet> result = processor.validate(idToken);

            log.debug("Get validate result {} from token {}.", result, idToken);
        }
    }
}
