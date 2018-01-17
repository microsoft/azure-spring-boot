/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.aad;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties("azureService")
public class ServiceEndpointsProperties {
    private static final String AZURE_AUTH_DOMAIN_CN = "login.partner.microsoftonline.cn";
    private static final String YML_ENDPOINTS_KEY_CN = "cn";
    private static final String YML_ENDPOINTS_KEY_GLOBAL = "global";

    private Map<String, ServiceEndpoints> endpoints = new HashMap<>();

    public Map<String, ServiceEndpoints> getEndpoints() {
        return endpoints;
    }

    public ServiceEndpoints getServiceEndpoints(String authUrl) {
        Assert.notEmpty(endpoints, "No service endpoints found");

        if (isAzureCN(authUrl)) {
            return endpoints.get(YML_ENDPOINTS_KEY_CN);
        }

        return endpoints.get(YML_ENDPOINTS_KEY_GLOBAL);
    }

    private boolean isAzureCN(String authUrl) {
        return authUrl != null && authUrl.contains(AZURE_AUTH_DOMAIN_CN);
    }
}
