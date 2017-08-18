/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.msgraph.api.impl;

import com.microsoft.azure.msgraph.api.CustomOperations;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

public class CustomTemplate extends AbstractMicrosoftOperations implements CustomOperations {
    private final MicrosoftTemplate microsoft;

    public CustomTemplate(MicrosoftTemplate microsoft, boolean authorized) {
        super(authorized);
        this.microsoft = microsoft;
    }

    @Override
    public RestTemplate getRestTemplate() {
        return microsoft.getRestTemplate();
    }

    @Override
    public URI getGraphAPIURI(String relativePath) {
        return microsoft.getGraphAPIURI(relativePath);
    }
}
