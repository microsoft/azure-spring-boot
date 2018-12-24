/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.b2c;

import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@NoArgsConstructor
public class AADB2CFilterScenarioHandlerChain {

    private AADB2CFilterScenarioHandler successor = new AADB2CFilterDefaultHandler();

    public void handle(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws AADB2CAuthenticationException, IOException, ServletException {
        successor.handle(request, response, chain);
    }

    private void insertHandlerAfter(@NonNull AADB2CFilterScenarioHandler after,
                                    @NonNull AADB2CFilterScenarioHandler handler) {
        final AADB2CFilterScenarioHandler successor = after.getSuccessor();

        after.setSuccessor(handler);
        handler.setSuccessor(successor);
    }

    private void insertHandlerHead(@NonNull AADB2CFilterScenarioHandler handler) {
        handler.setSuccessor(successor);
        successor = handler;
    }

    public void addHandler(@NonNull AADB2CFilterScenarioHandler handler) {
        if (handler instanceof AADB2CFilterForgotPasswordHandler) {
            insertHandlerHead(handler);
        } else if (successor instanceof AADB2CFilterDefaultHandler) {
            insertHandlerHead(handler);
        } else {
            insertHandlerAfter(successor, handler);
        }
    }
}
