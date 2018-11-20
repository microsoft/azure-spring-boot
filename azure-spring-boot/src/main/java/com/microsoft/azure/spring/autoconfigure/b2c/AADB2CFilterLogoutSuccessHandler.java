/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.b2c;

import lombok.NoArgsConstructor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@NoArgsConstructor
public class AADB2CFilterLogoutSuccessHandler implements AADB2CFilterScenarioHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response) {
        throw new UnsupportedOperationException("AADB2CFilterLogoutSuccessHandler implemented yet.");
    }
}
