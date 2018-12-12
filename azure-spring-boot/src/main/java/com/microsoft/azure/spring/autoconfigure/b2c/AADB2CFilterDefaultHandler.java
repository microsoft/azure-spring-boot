/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.b2c;

import org.springframework.util.Assert;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AADB2CFilterDefaultHandler extends AbstractAADB2CFilterScenarioHandler {

    @Override
    protected void handleInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        Assert.isTrue(matches(request), "Filter handler should match the scenario.");

        updateAuthentication();
        chain.doFilter(request, response);
    }

    @Override
    public Boolean matches(HttpServletRequest request) {
        return Boolean.TRUE;
    }
}
