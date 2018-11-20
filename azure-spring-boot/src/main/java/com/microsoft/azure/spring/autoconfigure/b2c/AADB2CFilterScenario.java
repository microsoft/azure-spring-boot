/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.b2c;

import lombok.Getter;
import lombok.NonNull;

import javax.servlet.http.HttpServletRequest;

public enum AADB2CFilterScenario {

    SIGN_IN_OR_SIGN_UP(new AADB2CFilterSignUpOrInHandler()),
    LOGOUT_SUCCESS(new AADB2CFilterLogoutSuccessHandler()),
    DEFAULT_SCENARIO(new AADB2CFilterDefaultHandler());

    @Getter
    private AADB2CFilterScenarioHandler scenarioHandler;

    AADB2CFilterScenario(@NonNull AADB2CFilterScenarioHandler scenarioHandler) {
        this.scenarioHandler = scenarioHandler;
    }

    public static AADB2CFilterScenario resolve(@NonNull HttpServletRequest request, @NonNull AADB2CFilter filter) {
        final String requestURL = request.getRequestURL().toString();

        if ("GET".equals(request.getMethod()) && requestURL.equals(filter.getSignUpOrInRedirectURL())) {
            return SIGN_IN_OR_SIGN_UP;
        } else if ("GET".equals(request.getMethod()) && requestURL.equals(filter.getLogoutSuccessURL())) {
            return LOGOUT_SUCCESS;
        }

        return DEFAULT_SCENARIO;
    }
}
