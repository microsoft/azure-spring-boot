/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.msgraph.api.impl;

import org.junit.Before;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

public class AbstractMicrosoftApiTest {
    protected MicrosoftTemplate microsoft;
    protected MockRestServiceServer mockServer;
    protected HttpHeaders responseHeaders;

    @Before
    public void setUp() throws Exception {
        microsoft = new MicrosoftTemplate("access token");
        mockServer = MockRestServiceServer.createServer(microsoft.getRestTemplate());
        responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
    }

    protected Resource jsonResource(String filename) {
        return new ClassPathResource(filename + ".json");
    }
}
