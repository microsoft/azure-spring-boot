package com.microsoft.azure.autoconfigure.iothub;

import static org.assertj.core.api.Java6Assertions.assertThat;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.microsoft.azure.sdk.iot.device.DeviceClient;

public class IoTHubAutoConfigurationTest {

    @Test
    public void cannotAutowireDeviceClientWithInvalidConnectionString() {
        System.setProperty(Constants.CONNECTION_STRING_PROPERTY, StringUtils.EMPTY);
        System.setProperty(Constants.PROTOCOL_NAME_PROPERTY, Constants.PROTOCOL_STRING);

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(IoTHubAutoConfiguration.class);
            context.refresh();

            DeviceClient deviceClient  = null;

            Exception exception = null;
            try {
                deviceClient = context.getBean(DeviceClient.class);
            } catch (Exception e) {
                exception = e;
            }

            assertThat(exception).isNotNull();
            assertThat(exception).isExactlyInstanceOf(BeanCreationException.class);

            assertThat(deviceClient).isNull();
        }

        System.clearProperty(Constants.CONNECTION_STRING_PROPERTY);
        System.clearProperty(Constants.PROTOCOL_NAME_PROPERTY);
    }
}
