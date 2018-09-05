/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.keyvault.spring;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@ConfigurationProperties("azure.keyvault")
public class KeyVaultProperties {
    @NotEmpty
    private String clientId;

    @NotEmpty
    private String clientKey;

    @NotEmpty
    private String name;

    private boolean enabled = true;

    private boolean allowTelemetry = true;

    private long refreshInterval = -1;

    private boolean useCache = true;
}
