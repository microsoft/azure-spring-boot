/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.aad;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.*;

public class UserPrincipal {

    private static final Logger LOG = LoggerFactory.getLogger(UserPrincipal.class);
    private ServiceEndpoints serviceEndpoints;
    private JWKSet jwsKeySet;
    private JWSObject jwsObject;
    private JWTClaimsSet jwtClaimsSet;
    private List<UserGroup> userGroups;

    public UserPrincipal() {
        jwsObject = null;
        jwtClaimsSet = null;
        userGroups = null;
        serviceEndpoints = new ServiceEndpoints();
    }

    public UserPrincipal(String idToken, ServiceEndpoints serviceEndpoints)
            throws MalformedURLException, ParseException, BadJOSEException, JOSEException {
        this.serviceEndpoints = serviceEndpoints;
        this.jwsKeySet = loadAadPublicKeys();
        final ConfigurableJWTProcessor<SecurityContext> validator = getAadJwtTokenValidator();
        jwtClaimsSet = validator.process(idToken, null);
        final JWTClaimsSetVerifier<SecurityContext> verifier = validator
                .getJWTClaimsSetVerifier();
        verifier.verify(jwtClaimsSet, null);
        jwsObject = JWSObject.parse(idToken);
        userGroups = null;
    }

    private JWKSet loadAadPublicKeys() {
        try {
            return JWKSet.load(new URL(serviceEndpoints.getAadKeyDiscoveryUri()));
        } catch (IOException | ParseException e) {
            LOG.error("Error loading AAD public keys: {}", e.getMessage());
        }
        return null;
    }

    // claimset
    public String getIssuer() {
        return jwtClaimsSet == null ? null : jwtClaimsSet.getIssuer();
    }

    public String getSubject() {
        return jwtClaimsSet == null ? null : jwtClaimsSet.getSubject();
    }

    public Map<String, Object> getClaims() {
        return jwtClaimsSet == null ? null : jwtClaimsSet.getClaims();
    }

    public Object getClaim() {
        return jwtClaimsSet == null ? null : jwtClaimsSet.getClaim("tid");
    }

    // header
    public String getKid() {
        return jwsObject == null ? null : jwsObject.getHeader().getKeyID();
    }

    // JWK
    public JWK getJWKByKid(String kid) {
        return jwsKeySet == null ? null : jwsKeySet.getKeyByKeyId(kid);
    }

    public void setUserGroups(List<UserGroup> groups) {
        this.userGroups = groups;
    }

    public List<UserGroup> getUserGroups() {
        return this.userGroups;
    }

    public boolean isMemberOf(UserGroup group) {
        return !(userGroups == null || userGroups.isEmpty()) && userGroups.contains(group);
    }

    private ConfigurableJWTProcessor<SecurityContext> getAadJwtTokenValidator()
            throws MalformedURLException {
        final ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
        final JWKSource<SecurityContext> keySource =
                new RemoteJWKSet<>(new URL(serviceEndpoints.getAadKeyDiscoveryUri()));
        final JWSAlgorithm expectedJWSAlg = JWSAlgorithm.RS256;
        final JWSKeySelector<SecurityContext> keySelector = new JWSVerificationKeySelector<>(expectedJWSAlg, keySource);
        jwtProcessor.setJWSKeySelector(keySelector);

        jwtProcessor.setJWTClaimsSetVerifier(new DefaultJWTClaimsVerifier<SecurityContext>() {
            @Override
            public void verify(JWTClaimsSet claimsSet, SecurityContext ctx) throws BadJWTException {
                super.verify(claimsSet, ctx);
                final String issuer = claimsSet.getIssuer();
                if (issuer == null || !issuer.contains("https://sts.windows.net/")
                        && !issuer.contains("https://sts.chinacloudapi.cn/")) {
                    throw new BadJWTException("Invalid token issuer");
                }
            }
        });
        return jwtProcessor;
    }
}

