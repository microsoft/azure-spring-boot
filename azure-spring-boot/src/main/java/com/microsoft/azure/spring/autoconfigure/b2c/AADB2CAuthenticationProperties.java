/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.b2c;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = AADB2CAuthenticationProperties.PREFIX)
public class AADB2CAuthenticationProperties {

    public static final String PREFIX = "azure.activedirectory.b2c";

    private static final String POLICIES = "policies";

    private static final String SIGN_UP_OR_SIGN_IN = "sign-up-or-sign-in";

    /**
     * We do not use ${@link String#format(String, Object...)}
     * as it's not real constant, which cannot be referenced in annotation.
     */
    private static final String POLICY_SIGN_UP_OR_SIGN_IN = POLICIES + "." + SIGN_UP_OR_SIGN_IN;

    public static final String POLICY_SIGN_UP_OR_SIGN_IN_NAME = POLICY_SIGN_UP_OR_SIGN_IN + ".name";

    public static final String POLICY_SIGN_UP_OR_SIGN_IN_REDIRECT_URL = POLICY_SIGN_UP_OR_SIGN_IN + ".redirect-url";

    /**
     * The name of the b2c tenant that created.
     */
    @NotBlank(message = "tenant name should not be blank")
    private String tenant;

    /**
     * The application ID that registered under b2c tenant.
     */
    @NotBlank(message = "client ID should not be blank")
    private String clientId;

    /**
     * The all polices that created under b2c tenant
     */
    @JsonProperty(POLICIES)
    private Policies policies = new Policies();

    @Getter
    @Validated
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Policies {

        /**
         * The sign-up-or-sign-in policies that created under b2c tenant.
         */
        @JsonProperty(SIGN_UP_OR_SIGN_IN)
        private Policy signUpOrSignIn = new Policy();

        // TODO(pan): will add more policies like sign-in, sign-up, profile-editing and password-reset.
    }

    @Getter
    @Setter
    @Validated
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Policy {

        /**
         * The name of policy that created under b2c tenant.
         */
        @NotBlank(message = "policy name should not be blank")
        private String name;

        /**
         * The redirect URL that configured under
         */
        @URL(message = "redirect URL should be valid")
        private String redirectUrl;
    }
}
