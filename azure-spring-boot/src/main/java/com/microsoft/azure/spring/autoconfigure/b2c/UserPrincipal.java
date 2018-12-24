/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.b2c;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

@Getter
@ToString
public class UserPrincipal implements Serializable {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final long serialVersionUID = 2194984731878145577L;

    private static final String ATTRIBUTE_SURNAME = "family_name";

    private static final String ATTRIBUTE_STREET_ADDRESS = "streetAddress";

    private static final String ATTRIBUTE_STATE = "state";

    private static final String ATTRIBUTE_POSTAL_CODE = "postalCode";

    private static final String ATTRIBUTE_JOB_TITLE = "jobTitle";

    private static final String ATTRIBUTE_GIVEN_NAME = "given_name";

    private static final String ATTRIBUTE_DISPLAY_NAME = "name";

    private static final String ATTRIBUTE_COUNTRY = "country";

    private static final String ATTRIBUTE_CITY = "city";

    private static final String ATTRIBUTE_NEW_USER = "newUser";

    private static final String ATTRIBUTE_TRUST_FRAMEWORK_POLICY = "tfp";

    private static final String ATTRIBUTE_EMAILS = "emails";

    private static final String ATTRIBUTE_CODE_HASH = "c_hash";

    private static final String ATTRIBUTE_NONCE = "nonce";

    private static final String ATTRIBUTE_ID_PROVIDER = "idp";

    // JWT token
    private final String keyId;

    private final String algorithm;

    private final String subject;

    private final String issuer;

    private final Date expireDate;

    private final Date notValidBeforeDate;

    private final Date issueAtDate;

    private final String nonce;

    private final List<String> audiences;

    private final Map<String, Object> claim;

    private final String codeHash;

    private final String code; // The authorization code.

    // AADB2C pre-defined User attributes.
    private final String city;

    private final String countryOrRegion;

    private final String displayName;

    private final List<String> emails;

    private final String givenName;

    private final String identityProvider;

    private final String jobTitle;

    private final String legalAgeGroup;

    private final String postalCode;

    private final String stateOrProvince;

    private final String street;

    private final String surname;

    private final Boolean isNew;

    private final String userObjectId;

    private final String trustFrameworkPolicy;

    @SuppressWarnings("unchecked")
    public UserPrincipal(@NonNull Pair<JWSObject, JWTClaimsSet> jwtToken, String code) {
        final JWSObject jwsObject = jwtToken.getLeft();
        final JWTClaimsSet jwtClaimsSet = jwtToken.getRight();

        this.keyId = jwsObject.getHeader().getKeyID();
        this.algorithm = jwsObject.getHeader().getAlgorithm().getName();
        this.subject = jwtClaimsSet.getSubject();
        this.issuer = jwtClaimsSet.getIssuer();
        this.audiences = jwtClaimsSet.getAudience();
        this.expireDate = jwtClaimsSet.getExpirationTime();
        this.notValidBeforeDate = jwtClaimsSet.getNotBeforeTime();
        this.issueAtDate = jwtClaimsSet.getIssueTime();
        this.claim = new HashMap<>(jwtClaimsSet.getClaims());
        this.code = code;

        this.userObjectId = jwtClaimsSet.getSubject(); // Subject populates the object ID of the user in the directory.

        this.surname = (String) getClaim().get(ATTRIBUTE_SURNAME);
        this.street = (String) getClaim().get(ATTRIBUTE_STREET_ADDRESS);
        this.stateOrProvince = (String) getClaim().get(ATTRIBUTE_STATE);
        this.postalCode = (String) getClaim().get(ATTRIBUTE_POSTAL_CODE);
        this.jobTitle = (String) getClaim().get(ATTRIBUTE_JOB_TITLE);
        this.givenName = (String) getClaim().get(ATTRIBUTE_GIVEN_NAME);
        this.displayName = (String) getClaim().get(ATTRIBUTE_DISPLAY_NAME);
        this.countryOrRegion = (String) getClaim().get(ATTRIBUTE_COUNTRY);
        this.city = (String) getClaim().get(ATTRIBUTE_CITY);
        this.trustFrameworkPolicy = (String) getClaim().get(ATTRIBUTE_TRUST_FRAMEWORK_POLICY);
        this.codeHash = (String) getClaim().get(ATTRIBUTE_CODE_HASH);
        this.nonce = (String) getClaim().get(ATTRIBUTE_NONCE);
        this.identityProvider = (String) getClaim().get(ATTRIBUTE_ID_PROVIDER);

        this.legalAgeGroup = "";

        List<String> emails;
        Boolean isNew;

        try {
            isNew = (Boolean) getClaim().get(ATTRIBUTE_NEW_USER);
        } catch (ClassCastException ignore) {
            isNew = false;
        }

        try {
            emails = (List<String>) OBJECT_MAPPER.readValue(getClaim().get(ATTRIBUTE_EMAILS).toString(), List.class);
        } catch (IOException ignore) {
            emails = new ArrayList<>();
        }

        this.emails = emails;
        this.isNew = isNew;
    }

    public Date getExpireDate() {
        return new Date(expireDate.getTime());
    }

    public Date getIssueAtDate() {
        return new Date(issueAtDate.getTime());
    }

    public Date getNotValidBeforeDate() {
        return new Date(notValidBeforeDate.getTime());
    }

    public boolean isUserExpired() {
        return getExpireDate().before(new Date());
    }

    public boolean isUserValid() {
        return new Date().after(getNotValidBeforeDate());
    }
}
