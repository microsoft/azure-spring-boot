/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.b2c;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

import static java.lang.String.format;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AADB2CURL {

    private static final String OPENID_PATTERN = "https://%s.b2clogin.com/%s.onmicrosoft.com/oauth2/v2.0/%s?" +
            "client_id=%s&" +
            "redirect_uri=%s&" +
            "response_mode=query&" +
            "response_type=code+id_token&" +
            "scope=openid&" +
            "state=%s&" +
            "nonce=%s&" +
            "p=%s";

    private static final String API_TYPE_AUTHORIZE = "authorize";

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
     * Get the openId sign up or sign in redirect URL based on ${@link AADB2CProperties}, encodes the
     * requestURL and UUID in state field.
     *
     * @param properties of ${@link AADB2CProperties}
     * @param requestURL from ${@link HttpServletRequest} that user attempt to access.
     * @return the URL of openid sign up or sign in.
     */
    public static String getOpenIdSignUpOrSignInUrl(@NonNull AADB2CProperties properties, String requestURL) {
        Assert.hasText(requestURL, "requestURL should have text.");

        final AADB2CProperties.Policy policy = properties.getPolicies().getSignUpOrSignIn();
        return format(OPENID_PATTERN,
                properties.getTenant(),
                properties.getTenant(),
                API_TYPE_AUTHORIZE,
                properties.getClientId(),
                getEncodedURL(policy.getRedirectURI()),
                getState(requestURL),
                getUUID(),
                policy.getName()
        );
    }
}
