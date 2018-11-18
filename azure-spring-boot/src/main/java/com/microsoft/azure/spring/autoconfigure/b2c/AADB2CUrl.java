/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.b2c;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static java.lang.String.format;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AADB2CUrl {

    private static final String API_PATTERN = "https://%s.b2clogin.com/%s.onmicrosoft.com/oauth2/v2.0/%s?";

    private static final String PARAMETER_PATTERN = "%s=%s";

    private static final String CLIENT_ID = "client-id";

    private static final String REDIRECT_URL = "redirect-url";

    private static final String RESPONSE_MODE = "response_mode";

    private static final String RESPONSE_TYPE = "response_type";

    private static final String SCOPE = "scope";

    private static final String STATE = "state";

    private static final String NONCE = "nonce";

    private static final String POLICY = "p";

    private static final String API_TYPE_AUTHORIZE = "authorize";

    private static final String RESPONSE_MODE_QUERY = "query";

    private static final String RESPONSE_TYPE_CODE = "code";

    private static final String RESPONSE_TYPE_ID_TOKEN = "id-token";

    private static final String SCOPE_OPENID = "openid";

    private static String getUUID() {
        return UUID.randomUUID().toString();
    }

    private static String getState(String requestUrl) {
        return String.join("-", getUUID(), requestUrl);
    }

    public static String toOpenIdSignUpOrSignInUrl(@NonNull AADB2CProperties properties, @NonNull String requestUrl) {
        final String endpoint = format(API_PATTERN, properties.getTenant(), properties.getTenant(), API_TYPE_AUTHORIZE);
        final AADB2CProperties.Policy policy = properties.getPolicies().getSignUpOrSignIn();

        final List<String> parameters = Arrays.asList(
                format(PARAMETER_PATTERN, CLIENT_ID, properties.getClientId()),
                format(PARAMETER_PATTERN, REDIRECT_URL, policy.getRedirectUrl()),
                format(PARAMETER_PATTERN, RESPONSE_MODE, RESPONSE_MODE_QUERY),
                format(PARAMETER_PATTERN, RESPONSE_TYPE, format("%s+%s", RESPONSE_TYPE_CODE, RESPONSE_TYPE_ID_TOKEN)),
                format(PARAMETER_PATTERN, SCOPE, SCOPE_OPENID),
                format(PARAMETER_PATTERN, STATE, getState(requestUrl)),
                format(PARAMETER_PATTERN, NONCE, getUUID()),
                format(PARAMETER_PATTERN, POLICY, policy.getName())
        );

        return endpoint + String.join("&", parameters);
    }
}
