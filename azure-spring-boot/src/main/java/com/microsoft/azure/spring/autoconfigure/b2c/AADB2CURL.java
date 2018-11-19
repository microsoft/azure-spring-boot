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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static java.lang.String.format;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AADB2CURL {

    private static final String API_PATTERN = "https://%s.b2clogin.com/%s.onmicrosoft.com/oauth2/v2.0/%s?";

    private static final String PARAMETER_PATTERN = "%s=%s";

    private static final String CLIENT_ID = "client_id";

    private static final String REDIRECT_URL = "redirect_uri";

    private static final String RESPONSE_MODE = "response_mode";

    private static final String RESPONSE_TYPE = "response_type";

    private static final String SCOPE = "scope";

    private static final String STATE = "state";

    private static final String NONCE = "nonce";

    private static final String POLICY = "p";

    private static final String API_TYPE_AUTHORIZE = "authorize";

    private static final String RESPONSE_MODE_QUERY = "query";

    private static final String RESPONSE_TYPE_CODE = "code";

    private static final String RESPONSE_TYPE_ID_TOKEN = "id_token";

    private static final String SCOPE_OPENID = "openid";

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
     * requestURL and UUID in ${@link AADB2CURL#SCOPE} field.
     *
     * @param properties of ${@link AADB2CProperties}
     * @param requestURL from ${@link HttpServletRequest} that user attempt to access.
     * @return
     */
    public static String getOpenIdSignUpOrSignInUrl(@NonNull AADB2CProperties properties, String requestURL) {
        Assert.hasText(requestURL, "requestURL should have text.");

        final String endpoint = format(API_PATTERN, properties.getTenant(), properties.getTenant(), API_TYPE_AUTHORIZE);
        final AADB2CProperties.Policy policy = properties.getPolicies().getSignUpOrSignIn();
        final List<String> parameters = Arrays.asList(
                // Each element is one parameter of redirect URL that take 'property=value' format.
                // For example, 'response_mode=query' and 'response_type=code+id_token'.
                format(PARAMETER_PATTERN, CLIENT_ID, properties.getClientId()),
                format(PARAMETER_PATTERN, REDIRECT_URL, getEncodedURL(policy.getRedirectURI())),
                format(PARAMETER_PATTERN, RESPONSE_MODE, RESPONSE_MODE_QUERY),
                format(PARAMETER_PATTERN, RESPONSE_TYPE, format("%s+%s", RESPONSE_TYPE_CODE, RESPONSE_TYPE_ID_TOKEN)),
                format(PARAMETER_PATTERN, SCOPE, SCOPE_OPENID),
                format(PARAMETER_PATTERN, STATE, getState(requestURL)),
                format(PARAMETER_PATTERN, NONCE, getUUID()),
                format(PARAMETER_PATTERN, POLICY, policy.getName())
        );

        return endpoint + String.join("&", parameters);
    }
}
