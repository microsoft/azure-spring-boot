package com.microsoft.azure.autoconfigure.iothub;

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("azure.iothub")
public class IoTHubProperties {
    @NotNull
    private String connectionString;
    private String protocol = "HTTPS";
    /**
     * @return the connectionString
     */
    public final String getConnectionString() {
        return connectionString;
    }
    /**
     * @param connectionString the connectionString to set
     */
    public final void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }
    /**
     * @return the protocol
     */
    public final String getProtocol() {
        return protocol;
    }
    /**
     * @param protocol the protocol to set
     */
    public final void setProtocol(String protocol) {
        this.protocol = protocol;
    }
}
