/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.autoconfigure.aad;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Map;

public final class AzureADJwtToken {
    private final JWSObject jwsObject;
    private final JWTClaimsSet jwsClaimsSet;
    private final JWKSet jwsKeySet;

    public AzureADJwtToken(String bearerToken) throws Exception {
        final ConfigurableJWTProcessor validator = getAadJwtTokenValidator(bearerToken);
        jwsClaimsSet = validator.process(bearerToken, null);
        final JWTClaimsSetVerifier verifier = validator.getJWTClaimsSetVerifier();
        verifier.verify(jwsClaimsSet, null);
        jwsObject = JWSObject.parse(bearerToken);
        jwsKeySet = loadAadPublicKeys();
    }

    private ConfigurableJWTProcessor getAadJwtTokenValidator(
            String bearerToken) throws ParseException, JOSEException, BadJOSEException, MalformedURLException {
        final ConfigurableJWTProcessor jwtProcessor = new DefaultJWTProcessor();
        final JWKSource keySource = new RemoteJWKSet(
                new URL("https://login.microsoftonline.com/common/discovery/keys"));
        final JWSAlgorithm expectedJWSAlg = JWSAlgorithm.RS256;
        final JWSKeySelector keySelector = new JWSVerificationKeySelector(expectedJWSAlg, keySource);
        jwtProcessor.setJWSKeySelector(keySelector);

        jwtProcessor.setJWTClaimsSetVerifier(new DefaultJWTClaimsVerifier(){
            @Override
            public void verify(JWTClaimsSet  claimsSet, SecurityContext ctx) throws BadJWTException {
                super.verify(claimsSet, ctx);
                final String issuer = claimsSet.getIssuer();
                if (issuer == null || !issuer.contains("https://sts.windows.net/")) {
                    throw new BadJWTException("Invalid token issuer");
                }
            }
        });
        return jwtProcessor;
    }

    private JWKSet loadAadPublicKeys() throws IOException, ParseException {
        final int connectTimeoutinMS = 1000;
        final int readTimeoutinMS = 1000;
        final int sizeLimitinBytes = 10000;
        return JWKSet.load(
                new URL("https://login.microsoftonline.com/common/discovery/keys"),
                connectTimeoutinMS,
                readTimeoutinMS,
                sizeLimitinBytes);
    }

    // claimset
    public String getIssuer() {
        return jwsClaimsSet == null ? null : jwsClaimsSet.getIssuer();
    }
    public String getSubject() {
        return jwsClaimsSet == null ? null : jwsClaimsSet.getSubject();
    }
    public Map<String, Object> getClaims()  {
        return jwsClaimsSet == null ? null : jwsClaimsSet.getClaims();
    }
    public Object getClaim(String name)  {
        return jwsClaimsSet == null ? null : jwsClaimsSet.getClaim(name);
    }

    // header
    public String getKid() {
        return jwsObject == null ? null : jwsObject.getHeader().getKeyID();
    }

    // JWK
    public JWK getJWKByKid(String kid) {
        return jwsKeySet == null ? null : jwsKeySet.getKeyByKeyId(kid);
    }

}

