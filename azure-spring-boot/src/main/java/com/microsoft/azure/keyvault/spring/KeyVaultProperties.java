package com.microsoft.azure.keyvault.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotEmpty;

@ConfigurationProperties("azure.keyvault")
public class KeyVaultProperties {
    @NotEmpty
    private String clientId;

    @NotEmpty
    private String clientKey;

    @NotEmpty
    private String uri;

    private boolean enabled = true;

    private boolean allowTelemetry = true;

    private long tokenAcquireTimeoutSeconds = 60L;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientKey() {
        return clientKey;
    }

    public void setClientKey(String clientKey) {
        this.clientKey = clientKey;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isAllowTelemetry() {
        return allowTelemetry;
    }

    public void setAllowTelemetry(boolean allowTelemetry) {
        this.allowTelemetry = allowTelemetry;
    }

    public long getTokenAcquireTimeoutSeconds() {
        return tokenAcquireTimeoutSeconds;
    }

    public void setTokenAcquireTimeoutSeconds(long tokenAcquireTimeoutSeconds) {
        this.tokenAcquireTimeoutSeconds = tokenAcquireTimeoutSeconds;
    }
}
