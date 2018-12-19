/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.aad;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.ResourceRetriever;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.*;
import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;

@Slf4j
public class UserPrincipalManager {

    private final JWKSource<SecurityContext> keySource;

    /**
     * Creates a new {@link UserPrincipalManager} with a predefined {@link JWKSource}.
     * <p>
     * This is helpful in cases the JWK is not a remote JWKSet or for unit testing.
     *
     * @param keySource - {@link JWKSource} containing at least one key
     */
    public UserPrincipalManager(JWKSource<SecurityContext> keySource) {
        this.keySource = keySource;
    }

    /**
     * Create a new {@link UserPrincipalManager} based of the {@link ServiceEndpoints#getAadKeyDiscoveryUri()} and
     * {@link AADAuthenticationProperties#getEnvironment()}.
     *
     * @param serviceEndpointsProps -  used to retrieve the JWKS URL
     * @param aadAuthProps          - used to retrieve the environment.
     * @param resourceRetriever     - configures the {@link RemoteJWKSet} call.
     */
    public UserPrincipalManager(ServiceEndpointsProperties serviceEndpointsProps,
                                AADAuthenticationProperties aadAuthProps,
                                ResourceRetriever resourceRetriever) {
        try {
            keySource = new RemoteJWKSet<>(new URL(serviceEndpointsProps
                    .getServiceEndpoints(aadAuthProps.getEnvironment()).getAadKeyDiscoveryUri()), resourceRetriever);
        } catch (MalformedURLException e) {
            log.error("Failed to parse active directory key discovery uri.", e);
            throw new IllegalStateException("Failed to parse active directory key discovery uri.", e);
        }
    }

    public UserPrincipal buildUserPrincipal(String idToken) throws ParseException, JOSEException, BadJOSEException {
        final JWSObject jwsObject = JWSObject.parse(idToken);
        final ConfigurableJWTProcessor<SecurityContext> validator =
                getAadJwtTokenValidator(jwsObject.getHeader().getAlgorithm());
        final JWTClaimsSet jwtClaimsSet = validator.process(idToken, null);
        final JWTClaimsSetVerifier<SecurityContext> verifier = validator.getJWTClaimsSetVerifier();
        verifier.verify(jwtClaimsSet, null);

        return new UserPrincipal(jwsObject, jwtClaimsSet);
    }

    private ConfigurableJWTProcessor<SecurityContext> getAadJwtTokenValidator(JWSAlgorithm jwsAlgorithm) {
        final ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();

        final JWSKeySelector<SecurityContext> keySelector =
                new JWSVerificationKeySelector<>(jwsAlgorithm, keySource);
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
