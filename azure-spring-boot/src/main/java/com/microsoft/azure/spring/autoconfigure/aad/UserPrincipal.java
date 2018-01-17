/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.aad;

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
import com.nimbusds.jwt.proc.BadJWTException;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.jwt.proc.JWTClaimsSetVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

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

    public List<UserGroup> getGroups(String graphApiToken) throws Exception {
        if (userGroups == null) {
            userGroups = loadUserGroups(graphApiToken);
        }
        return userGroups;
    }

    public boolean isMemberOf(UserGroup group) {
        return !(userGroups == null || userGroups.isEmpty()) && userGroups.contains(group);
    }

    public List<GrantedAuthority> getAuthoritiesByUserGroups(List<UserGroup> userGroups,
                                                             List<String> targetdGroupNames) {
        if (userGroups == null || targetdGroupNames == null || userGroups.isEmpty()
                || targetdGroupNames.isEmpty()) {
            return Collections.<GrantedAuthority>emptyList();
        }
        return userGroups.stream()
                .filter(usergroup -> targetdGroupNames
                        .contains(usergroup.getDisplayName()))
                .map(usergroup -> "ROLE_" + usergroup.getDisplayName())
                .map(SimpleGrantedAuthority::new).collect(toList());
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities();
    }

    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
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

    private List<UserGroup> loadUserGroups(String graphApiToken) throws Exception {
        final String responseInJson =
                AzureADGraphClient.getUserMembershipsV1(graphApiToken, serviceEndpoints.getAadMembershipRestUri());
        final List<UserGroup> lUserGroups = new ArrayList<>();
        final ObjectMapper objectMapper = JacksonObjectMapperFactory.getInstance();
        final JsonNode rootNode = objectMapper.readValue(responseInJson, JsonNode.class);
        final JsonNode valuesNode = rootNode.get("value");
        int i = 0;
        while (valuesNode != null
                && valuesNode.get(i) != null) {
            if (valuesNode.get(i).get("objectType").asText().equals("Group")) {
                lUserGroups.add(new UserGroup(
                        valuesNode.get(i).get("objectId").asText(),
                        valuesNode.get(i).get("displayName").asText()));
            }
            i++;
        }
        return lUserGroups;
    }
}

