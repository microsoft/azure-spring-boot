/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.b2c;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AADB2CURL {

    private static final String AUTHORIZATION_URI_PATTERN =
            "https://%s.b2clogin.com/%s.onmicrosoft.com/oauth2/v2.0/authorize";

    private static final String TOKEN_URI_PATTERN =
            "https://%s.b2clogin.com/%s.onmicrosoft.com/oauth2/v2.0/token?p=%s";

    private static final String JWKSET_URI_PATTERN =
            "https://%s.b2clogin.com/%s.onmicrosoft.com/discovery/v2.0/keys?p=%s";

    public static String getAuthorizationUri(String tenant) {
        Assert.hasText(tenant, "tenant should have text.");

        return String.format(AUTHORIZATION_URI_PATTERN, tenant, tenant);
    }

    public static String getTokenUri(String tenant, String policyName) {
        Assert.hasText(tenant, "tenant should have text.");
        Assert.hasText(policyName, "policy name should have text.");

        return String.format(TOKEN_URI_PATTERN, tenant, tenant, policyName);
    }

    public static String getJwkSetUri(String tenant, String policyName) {
        Assert.hasText(tenant, "tenant should have text.");
        Assert.hasText(policyName, "policy name should have text.");

        return String.format(JWKSET_URI_PATTERN, tenant, tenant, policyName);
    }
}
