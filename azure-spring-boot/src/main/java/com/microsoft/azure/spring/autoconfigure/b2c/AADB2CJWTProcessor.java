/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.b2c;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.BadJWTException;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.validator.constraints.URL;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

public class AADB2CJWTProcessor {

    private final String configURL;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final ConfigurableJWTProcessor<SecurityContext> processor = new DefaultJWTProcessor<>();

    public AADB2CJWTProcessor(@URL String url, @NonNull AADB2CProperties b2cProperties) {
        this.configURL = url;
        this.processor.setJWTClaimsSetVerifier(new JWTClaimsVerifier(b2cProperties));
    }

    private JWKSource<SecurityContext> getAADB2CKeySource() throws AADB2CAuthenticationException {
        try {
            final Configuration config = OBJECT_MAPPER.readValue(new java.net.URL(configURL), Configuration.class);

            return new ImmutableJWKSet<>(JWKSet.load(new java.net.URL(config.getJwksURI())));
        } catch (IOException | ParseException e) {
            throw new AADB2CAuthenticationException("Fail to get keys from URL: " + configURL, e);
        }
    }

    /**
     * Validate the given idToken with ${@link ConfigurableJWTProcessor}.
     *
     * @param idToken from ${@link AADB2CFilterScenarioHandler}
     * @return the parsed ${@link Pair} of one JWT token.
     * @throws AADB2CAuthenticationException when ${@link JWSObject#parse(String)},
     *                                       ${@link ConfigurableJWTProcessor#process(JWT, SecurityContext)} or
     *                                       ${@link JWTClaimsVerifier#verify(JWTClaimsSet)} failure.
     */
    public Pair<JWSObject, JWTClaimsSet> validate(String idToken) throws AADB2CAuthenticationException {
        Assert.hasText(idToken, "id_token should contains text.");

        try {
            final JWSObject jwsObject = JWSObject.parse(idToken);
            final JWSAlgorithm jwsAlgorithm = jwsObject.getHeader().getAlgorithm();

            processor.setJWSKeySelector(new JWSVerificationKeySelector<>(jwsAlgorithm, getAADB2CKeySource()));

            final JWTClaimsSet claimsSet = processor.process(idToken, null);

            processor.getJWTClaimsSetVerifier().verify(claimsSet, null);

            return Pair.of(jwsObject, claimsSet);
        } catch (ParseException | BadJOSEException | JOSEException e) {
            throw new AADB2CAuthenticationException("Failed to process ClaimsSet from id_token: " + idToken, e);
        }
    }

    @Data
    @NoArgsConstructor
    private static class Configuration {

        private String issuer;

        @JsonProperty("authorization_endpoint")
        private String authorizationEndpoint;

        @JsonProperty("token_endpoint")
        private String tokenEndpoint;

        @JsonProperty("end_session_endpoint")
        private String endSessionEndpoint;

        @JsonProperty("jwks_uri")
        private String jwksURI;

        @JsonProperty("response_modes_supported")
        private List<String> responseModes;

        @JsonProperty("response_types_supported")
        private List<String> responseTypes;

        @JsonProperty("scopes_supported")
        private List<String> scopes;

        @JsonProperty("subject_types_supported")
        private List<String> subjectTypes;

        @JsonProperty("id_token_signing_alg_values_supported")
        private List<String> signAlgorithm;

        @JsonProperty("token_endpoint_auth_methods_supported")
        private List<String> authenticationMethod;

        @JsonProperty("claims_supported")
        private List<String> claims;
    }

    private static class JWTClaimsVerifier extends DefaultJWTClaimsVerifier<SecurityContext> {

        private final AADB2CProperties b2cProperties;

        protected JWTClaimsVerifier(@NonNull AADB2CProperties b2cProperties) {
            this.b2cProperties = b2cProperties;
        }

        @Override
        public void verify(@NonNull JWTClaimsSet claimsSet, @Nullable SecurityContext context) throws BadJWTException {
            super.verify(claimsSet, context);

            final String clientId = b2cProperties.getClientId();

            if (!claimsSet.getAudience().contains(clientId)) {
                throw new BadJWTException("Audience value mismatch.");
            } else if (!claimsSet.getIssuer().contains(String.format("%s.b2clogin.com", b2cProperties.getTenant()))) {
                throw new BadJWTException("Unexpected issuer.");
            } else if (claimsSet.getExpirationTime().before(new Date())) {
                throw new BadJWTException("Token expired.");
            } else if (claimsSet.getIssueTime().after(claimsSet.getExpirationTime())) {
                throw new BadJWTException("Invalid issue time.");
            }
        }
    }
}
