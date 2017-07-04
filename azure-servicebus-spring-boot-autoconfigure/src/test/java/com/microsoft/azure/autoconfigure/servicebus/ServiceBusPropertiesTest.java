/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.autoconfigure.servicebus;

import com.microsoft.azure.servicebus.ReceiveMode;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

public class ServiceBusPropertiesTest {

    private static final String QUEUE_CONNECTION_STRING = "queue connection string";
    private static final ReceiveMode QUEUE_RECEIVE_MODE = ReceiveMode.PeekLock;

    @BeforeClass
    public static void beforeClass() {
        System.setProperty(Constants.QUEUE_CONNECTION_STRING_PROPERTY, QUEUE_CONNECTION_STRING);
        System.setProperty(Constants.QUEUE_RECEIVE_MODE_PROPERTY, QUEUE_RECEIVE_MODE.name());
    }

    @Test
    public void canSetQueueProperties() {
        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(Config.class);
        context.refresh();
        final ServiceBusProperties properties = context.getBean(ServiceBusProperties.class);

        assertThat(properties.getQueueConnectionString()).isEqualTo(QUEUE_CONNECTION_STRING);
        assertThat(properties.getQueueReceiveMode()).isEqualTo(QUEUE_RECEIVE_MODE);
    }

    @Configuration
    @EnableConfigurationProperties(ServiceBusProperties.class)
    static class Config {
    }
}
