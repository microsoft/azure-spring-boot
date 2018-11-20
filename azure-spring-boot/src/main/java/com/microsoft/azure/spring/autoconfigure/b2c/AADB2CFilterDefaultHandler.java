/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.b2c;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@NoArgsConstructor
public class AADB2CFilterDefaultHandler implements AADB2CFilterScenarioHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response) {
        log.debug("Currently there is nothing need to do for AADB2CFilterDefaultHandler.");
    }
}
