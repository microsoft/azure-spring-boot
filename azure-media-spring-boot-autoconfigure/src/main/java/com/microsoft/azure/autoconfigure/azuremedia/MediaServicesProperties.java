/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.autoconfigure.azuremedia;

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("azure.media")
public class MediaServicesProperties {

    @NotNull
    private String mediaServiceUri;
    @NotNull
    private String oAuthUri;
    @NotNull
    private String clientId;
    @NotNull
    private String clientSecret;
    @NotNull
    private String scope;

    /**
     * @return the mediaServiceUri
     */
    public String getMediaServiceUri() {
        return mediaServiceUri;
    }

    /**
     * @param mediaServiceUri the mediaServiceUri to set
     */
    public void setMediaServiceUri(String mediaServiceUri) {
        this.mediaServiceUri = mediaServiceUri;
    }

    /**
     * @return the oAuthUri
     */
    public String getoAuthUri() {
        return oAuthUri;
    }

    /**
     * @param oAuthUri the oAuthUri to set
     */
    public void setoAuthUri(String oAuthUri) {
        this.oAuthUri = oAuthUri;
    }

    /**
     * @return the clientId
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * @param clientId the clientId to set
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * @return the clientSecret
     */
    public String getClientSecret() {
        return clientSecret;
    }

    /**
     * @param clientSecret the clientSecret to set
     */
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    /**
     * @return the scope
     */
    public String getScope() {
        return scope;
    }

    /**
     * @param scope the scope to set
     */
    public void setScope(String scope) {
        this.scope = scope;
    }
}
