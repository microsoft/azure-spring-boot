/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.autoconfigure.mediaservices;

import javax.validation.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("azure.mediaservices")
public class MediaServicesProperties {
    @NotEmpty(message = "azure.mediaservices.tenant property must be configured.")
    @Getter
    @Setter
    private String tenant;

    /**
     * Media service Azure Active Directory client-id(application id).
     */
    @NotEmpty(message = "azure.mediaservices.client-id property must be configured.")
    @Getter
    @Setter
    private String clientId;

    /**
     * Media service Azure Active Directory client secret.
     */
    @NotEmpty(message = "azure.mediaservices.client-secret property must be configured.")
    @Getter
    @Setter
    private String clientSecret;

    /**
     * Media service REST API endpoint.
     */
    @NotEmpty(message = "azure.mediaservices.rest-api-endpoint property must be configured.")
    @Getter
    @Setter
    private String restApiEndpoint;

    /**
     * Proxy host if to use proxy.
     */
    @Getter
    @Setter
    private String proxyHost;

    /**
     * Proxy port if to use proxy.
     */
    @Getter
    @Setter
    private Integer proxyPort;

    /**
     * Proxy scheme if to use proxy. Default is http.
     */
    @Getter
    @Setter
    private String proxyScheme = "http";

    /**
     * Whether allow telemetry collecting.
     */
    @Getter
    @Setter
    private boolean allowTelemetry = true;

    /**
     * Socket connect timeout
     */
    @Getter
    @Setter
    private Integer connectTimeout;

    /**
     * Socket read timeout
     */
    @Getter
    @Setter
    private Integer readTimeout;

}
