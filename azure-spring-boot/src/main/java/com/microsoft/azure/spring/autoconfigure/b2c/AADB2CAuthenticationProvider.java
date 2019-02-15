/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.b2c;

import org.springframework.lang.NonNull;

public class AADB2CAuthenticationProvider {

    private final AADB2CProperties properties;

    private final AADB2CLogoutSuccessHandler handler;

    private final AADB2CAuthorizationRequestResolver resolver;

    public AADB2CAuthenticationProvider(AADB2CProperties properties, AADB2CLogoutSuccessHandler handler,
                                        AADB2CAuthorizationRequestResolver resolver) {
        this.properties = properties;
        this.handler = handler;
        this.resolver = resolver;
    }

    @NonNull
    public String getLoginProcessingUrl() {
        return properties.getLoginProcessingUrl();
    }

    @NonNull
    public AADB2CLogoutSuccessHandler getLogoutSuccessHandler() {
        return handler;
    }

    @NonNull
    public AADB2CAuthorizationRequestResolver getAuthorizationRequestResolver() {
        return resolver;
    }
}
