/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.aad;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@ConfigurationProperties("azure.activedirectory")
public class AADAuthenticationFilterProperties {
    /**
     * Registered application ID in Azure AD.
     */
    @NotEmpty
    private String clientId;
    /**
     * API Access Key of the registered application.
     */
    @NotEmpty
    private String clientSecret;
    /**
     * Azure AD groups.
     */
    @NotEmpty
    private List<String> activeDirectoryGroups;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public List<String> getActiveDirectoryGroups() {
        return activeDirectoryGroups;
    }

    public void setactiveDirectoryGroups(List<String> activeDirectoryGroups) {
        this.activeDirectoryGroups = activeDirectoryGroups;
    }
}
