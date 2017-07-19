/**
 * 
 */
package com.microsoft.azure.autoconfigure.iothub;

import java.net.URISyntaxException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.microsoft.azure.sdk.iot.device.DeviceClient;
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol;

/**
 * @author Juergen Mayrbaeurl
 *
 */
@Configuration
@EnableConfigurationProperties(IoTHubProperties.class)
public class IoTHubAutoConfiguration {
    
    private static final Logger LOG = LoggerFactory.getLogger(IoTHubAutoConfiguration.class);

    private final IoTHubProperties properties;

    /**
     * @param properties
     */
    public IoTHubAutoConfiguration(IoTHubProperties properties) {
        super();
        this.properties = properties;
    }
    
    @Bean
    @Scope("prototype")
    @ConditionalOnMissingBean
    public DeviceClient deviceClient() {
        
        DeviceClient result = null;
        
        // Check for properties availability
        if (this.properties == null) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Can't create DeviceClient instance for IoT Hub. Missing properties");
            }
            
            throw new IllegalStateException("Can't create DeviceClient instance for IoT Hub. Missing properties");
        }
        
        // Check for the existence of the IoT Hub Connection string in the properties
        if (StringUtils.isEmpty(this.properties.getConnectionString())) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Can't create DeviceClient instance for IoT Hub. No connection string specified");
            }
            
            throw new IllegalStateException(
                    "Can't create DeviceClient instance for IoT Hub. No connection string specified");
        }
        
        // Check for a valid IoT Hub protocol specification
        if (StringUtils.isEmpty(this.properties.getProtocol())) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Can't create DeviceClient instance for IoT Hub. No protocol string specified");
            }
            
            throw new IllegalStateException(
                    "Can't create DeviceClient instance for IoT Hub. No protocol string specified");
        } else {
            if (IotHubClientProtocol.valueOf(this.properties.getProtocol()) == null) {
                if (LOG.isErrorEnabled()) {
                    LOG.error("Can't create DeviceClient instance for IoT Hub. Unknown protocol '"
                            + this.properties.getProtocol() + "' specified");
                }
                
                throw new IllegalStateException("Can't create DeviceClient instance for IoT Hub. Unknown protocol '"
                        + this.properties.getProtocol() + "' specified");
            }
        }
        
        if (LOG.isDebugEnabled()) {
            LOG.debug("Now creating DeviceClient class for IoT Hub with connection string '"
                    + this.properties.getConnectionString() + "' and protocol '"
                    + this.properties.getProtocol() + "'");
        }
        
        try {
            result = new DeviceClient(this.properties.getConnectionString(), 
                    IotHubClientProtocol.valueOf(this.properties.getProtocol()));
        } catch (URISyntaxException e) {

            if (LOG.isErrorEnabled()) {
                LOG.error("Exception on creating DeviceClient object. Invalid URI specified", e);
            }
            
            throw new RuntimeException("Exception on creating DeviceClient object. Invalid URI specified", e);
        }
        
        return result;
    }
}
