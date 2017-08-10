/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.autoconfigure.aad;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class UserPrincipal {

    private JWSObject jwsObject;
    private JWTClaimsSet jwtClaimsSet;
    private JWKSet jwsKeySet;
    private List<UserGroup> userGroups;

    public UserPrincipal() {
        jwsObject = null;
        jwtClaimsSet = null;
        jwsKeySet = null;
        userGroups = null;
    }

    public UserPrincipal(String bearerToken) throws Exception {
        final ConfigurableJWTProcessor validator = getAadJwtTokenValidator(bearerToken);
        jwtClaimsSet = validator.process(bearerToken, null);
        final JWTClaimsSetVerifier verifier = validator.getJWTClaimsSetVerifier();
        verifier.verify(jwtClaimsSet, null);
        jwsObject = JWSObject.parse(bearerToken);
        jwsKeySet = loadAadPublicKeys();
    }

    public void setJwsObject(JWSObject jwsObject) {
        this.jwsObject = jwsObject;
    }

    public void setjwtClaimsSet(JWTClaimsSet jwtClaimsSet) {
        this.jwtClaimsSet = jwtClaimsSet;
    }

    public void setJwsKeySet(JWKSet jwsKeySet) {
        this.jwsKeySet = jwsKeySet;
    }

    public void setUserGroups(List<UserGroup> userGroups) {
        this.userGroups = userGroups;
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

    public Object getClaim(String name) {
        return jwtClaimsSet == null ? null : jwtClaimsSet.getClaim(name);
    }

    // header
    public String getKid() {
        return jwsObject == null ? null : jwsObject.getHeader().getKeyID();
    }

    // JWK
    public JWK getJWKByKid(String kid) {
        return jwsKeySet == null ? null : jwsKeySet.getKeyByKeyId(kid);
    }

    public List<UserGroup> getGroups(String idToken) throws Exception {
        if (userGroups == null || userGroups.isEmpty()) {
            final String responseInJson = AzureADGraphClient.getUserMembershipsV1(idToken);
            userGroups = new ArrayList<UserGroup>();
            final ObjectMapper objectMapper = JacksonObjectMapperFactory.getInstance();
            final JsonNode rootNode = objectMapper.readValue(responseInJson, JsonNode.class);
            final JsonNode valuesNode = rootNode.get("value");
            int i = 0;
            while (valuesNode != null
                    && valuesNode.get(i) != null) {
                if (valuesNode.get(i).get("objectType").asText().equals("Group")) {
                    userGroups.add(new UserGroup(
                            valuesNode.get(i).get("odata.type").asText(),
                            valuesNode.get(i).get("objectType").asText(),
                            valuesNode.get(i).get("description").asText(),
                            valuesNode.get(i).get("displayName").asText()));
                }
                i++;
            }
        }
        return userGroups;
    }

    public boolean isMemberOf(UserGroup group) {
        return userGroups == null || userGroups.isEmpty() ? false : userGroups.contains(group);
    }

    public Collection<? extends GrantedAuthority> getAuthoritiesByUserGroups(List<UserGroup> userGroups,
                                                                             List<String> targetdGroupNames) {
        final List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (final UserGroup group : userGroups) {
            if (targetdGroupNames.contains(group.getDisplayName())) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + group.getDisplayName()));
            }
        }
        return authorities;
    }
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities();
    }

    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private ConfigurableJWTProcessor getAadJwtTokenValidator(
            String bearerToken) throws ParseException, JOSEException, BadJOSEException, MalformedURLException {
        final ConfigurableJWTProcessor jwtProcessor = new DefaultJWTProcessor();
        final JWKSource keySource = new RemoteJWKSet(
                new URL("https://login.microsoftonline.com/common/discovery/keys"));
        final JWSAlgorithm expectedJWSAlg = JWSAlgorithm.RS256;
        final JWSKeySelector keySelector = new JWSVerificationKeySelector(expectedJWSAlg, keySource);
        jwtProcessor.setJWSKeySelector(keySelector);

        jwtProcessor.setJWTClaimsSetVerifier(new DefaultJWTClaimsVerifier() {
            @Override
            public void verify(JWTClaimsSet claimsSet, SecurityContext ctx) throws BadJWTException {
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

}

