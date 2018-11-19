/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.b2c;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AADB2CURL {

    // 'p' is the abbreviation for 'policy'.
    private static final String OPENID_AUTHORIZE_PATTERN =
            "https://%s.b2clogin.com/%s.onmicrosoft.com/oauth2/v2.0/authorize?" +
                    "client_id=%s&" +
                    "redirect_uri=%s&" +
                    "response_mode=query&" +
                    "response_type=code+id_token&" +
                    "scope=openid&" +
                    "state=%s&" +
                    "nonce=%s&" +
                    "p=%s";

    private static final String OPENID_LOGOUT_PATTERN =
            "https://%s.b2clogin.com/%s.onmicrosoft.com/oauth2/v2.0/logout?" +
                    "post_logout_redirect_uri=%s&" +
                    "p=%s";

    private static String getUUID() {
        return UUID.randomUUID().toString();
    }

    private static String getEncodedURL(@NonNull String url) {
        try {
            return URLEncoder.encode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new AADB2CConfigurationException("failed to encode url: " + url, e);
        }
    }

    public static void validateURL(String url) {
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            throw new AADB2CConfigurationException("Invalid logoutSuccessUrl: " + url, e);
        }
    }

    /**
     * Take state's format as UUID-RequestURI when redirect to sign-in URL.
     *
     * @param requestURL from ${@link HttpServletRequest} that user attempt to access.
     * @return the encoded state String.
     */
    private static String getState(String requestURL) {
        return String.join("-", getUUID(), getEncodedURL(requestURL));
    }

    /**
     * Get the openid sign up or sign in redirect URL based on ${@link AADB2CProperties}, encodes the
     * requestURL and UUID in state field.
     *
     * @param properties of ${@link AADB2CProperties}.
     * @param requestURL from ${@link HttpServletRequest} that user attempt to access.
     * @return the URL of openid sign up or sign in.
     */
    public static String getOpenIdSignUpOrSignInURL(@NonNull AADB2CProperties properties, String requestURL) {
        validateURL(requestURL);

        final AADB2CProperties.Policy policy = properties.getPolicies().getSignUpOrSignIn();
        return String.format(OPENID_AUTHORIZE_PATTERN,
                properties.getTenant(),
                properties.getTenant(),
                properties.getClientId(),
                getEncodedURL(policy.getRedirectURI()),
                getState(requestURL),
                getUUID(),
                policy.getName()
        );
    }

    /**
     * Get the openid logout URL based on ${@link AADB2CProperties}.
     *
     * @param properties  of ${@link AADB2CProperties}.
     * @param redirectURL from ${@link AADB2CLogoutSuccessHandler#getLogoutSuccessURL()}.
     * @return the URL of openid logout.
     */
    public static String getOpenIdLogoutURL(@NonNull AADB2CProperties properties, String redirectURL) {
        validateURL(redirectURL);

        return String.format(OPENID_LOGOUT_PATTERN,
                properties.getTenant(),
                properties.getTenant(),
                getEncodedURL(redirectURL),
                properties.getPolicies().getSignUpOrSignIn().getName()
        );
    }
}
