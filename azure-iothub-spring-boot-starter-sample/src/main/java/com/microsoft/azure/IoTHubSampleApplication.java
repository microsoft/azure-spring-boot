/**
 * 
 */
package com.microsoft.azure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.microsoft.azure.sdk.iot.device.DeviceClient;
import com.microsoft.azure.sdk.iot.device.Message;

/**
 * @author jurgenma
 *
 */
@SpringBootApplication
public class IoTHubSampleApplication implements CommandLineRunner {

    @Autowired
    private DeviceClient deviceClient;
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        
        SpringApplication.run(IoTHubSampleApplication.class);
    }

    @Override
    public void run(String... arg0) throws Exception {
        
        this.deviceClient.open();
        this.deviceClient.sendEventAsync(new Message("This is a test"), null, null);
        this.deviceClient.closeNow();
    }

}
