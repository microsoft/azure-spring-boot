/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.b2c;

import lombok.*;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthorizationCodeAuthenticationProvider;
import org.springframework.security.oauth2.client.oidc.authentication.OidcAuthorizationCodeAuthenticationProvider;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import java.net.MalformedURLException;

@Getter
@Setter
@Validated
@NoArgsConstructor
@ConfigurationProperties(prefix = AADB2CProperties.PREFIX)
public class AADB2CProperties {

    private static final String POLICIES = "policies";

    /**
     * We do not use ${@link String#format(String, Object...)}
     * as it's not real constant, which cannot be referenced in annotation.
     */
    private static final String POLICY_PASSWORD_RESET = POLICIES + ".password-reset";

    private static final String POLICY_PROFILE_EDIT = POLICIES + ".profile-edit";

    private static final String POLICY_SIGN_UP_OR_SIGN_IN = POLICIES + ".sign-up-or-sign-in";

    public static final String PREFIX = "azure.activedirectory.b2c";

    public static final String LOGOUT_SUCCESS_URL = "logout-success-url";

    public static final String POLICY_SIGN_UP_OR_SIGN_IN_NAME = POLICY_SIGN_UP_OR_SIGN_IN + ".name";

    public static final String POLICY_SIGN_UP_OR_SIGN_IN_REPLY_URL = POLICY_SIGN_UP_OR_SIGN_IN + ".reply-url";

    public static final String POLICY_PASSWORD_RESET_NAME = POLICY_PASSWORD_RESET + ".name";

    public static final String POLICY_PASSWORD_RESET_REPLY_URL = POLICY_PASSWORD_RESET + ".reply-url";

    public static final String POLICY_PROFILE_EDIT_NAME = POLICY_PROFILE_EDIT + ".name";

    public static final String POLICY_PROFILE_EDIT_REPLY_URL = POLICY_PROFILE_EDIT + ".reply-url";

    public static final String PASSWORD_RESET_PROCESS_URL = "password-reset-process-url";

    public static final String PROFILE_EDIT_PROCESS_URL = "profile-edit-process-url";

    /**
     * The name of the b2c tenant.
     */
    @NotBlank(message = "tenant name should not be blank")
    private String tenant;

    /**
     * Use OIDC ${@link OidcAuthorizationCodeAuthenticationProvider} by default. If set to false,
     * will use Oauth2 ${@link OAuth2AuthorizationCodeAuthenticationProvider}.
     */
    private Boolean oidcEnabled = true;

    /**
     * The application ID that registered under b2c tenant.
     */
    @NotBlank(message = "client ID should not be blank")
    private String clientId;

    /**
     * The application ID that registered under b2c tenant.
     */
    @NotBlank(message = "client secret should not be blank")
    private String clientSecret;

    @URL(message = "reply URL should not be blank")
    private String logoutSuccessUrl;

    @URL(message = "reply URL should not be blank")
    private String passwordResetProcessUrl;

    @URL(message = "reply URL should not be blank")
    private String profileEditProcessUrl;

    /**
     * The all polices which is created under b2c tenant.
     */
    private Policies policies = new Policies();

    private String getReplyURLPath(@URL String replyURL) {
        try {
            return new java.net.URL(replyURL).getPath();
        } catch (MalformedURLException e) {
            throw new AADB2CConfigurationException("Failed to get path of given URL.", e);
        }
    }

    @NonNull
    public String getPolicySignUpOrSignInReplyUrlPath() {
        return getReplyURLPath(policies.getSignUpOrSignIn().replyURL);
    }

    @Getter
    @Validated
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    protected static class Policies {

        /**
         * The sign-up-or-sign-in policy which is created under b2c tenant.
         */
        private Policy signUpOrSignIn = new Policy();

        /**
         * The password-reset policy which is created under b2c tenant.
         */
        private Policy passwordReset = new Policy();

        /**
         * The password-reset policy which is created under b2c tenant.
         */
        private Policy profileEdit = new Policy();
    }

    @Getter
    @Setter
    @Validated
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    protected static class Policy {

        /**
         * The name of policy which is created under b2c tenant.
         */
        @NotBlank(message = "policy name should not be blank")
        private String name;

        /**
         * The redirect URI which is configured under b2c tenant.
         */
        @URL(message = "reply URL should not be blank")
        private String replyURL;
    }
}
