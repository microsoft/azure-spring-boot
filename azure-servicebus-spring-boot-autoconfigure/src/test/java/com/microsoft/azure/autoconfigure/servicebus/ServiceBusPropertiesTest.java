/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.autoconfigure.servicebus;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

public class ServiceBusPropertiesTest {

    @BeforeClass
    public static void beforeClass() {
        System.setProperty(Constants.CONNECTION_STRING_PROPERTY, Constants.CONNECTION_STRING);
        System.setProperty(Constants.QUEUE_NAME_PROPERTY, Constants.QUEUE_NAME);
        System.setProperty(Constants.QUEUE_RECEIVE_MODE_PROPERTY, Constants.QUEUE_RECEIVE_MODE.name());
        System.setProperty(Constants.TOPIC_NAME_PROPERTY, Constants.TOPIC_NAME);
        System.setProperty(Constants.SUBSCRIPTION_NAME_PROPERTY, Constants.SUBSCRIPTION_NAME);
        System.setProperty(Constants.SUBSCRIPTION_RECEIVE_MODE_PROPERTY, Constants.SUBSCRIPTION_RECEIVE_MODE.name());
    }

    @Test
    public void canSetQueueProperties() {
        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(Config.class);
        context.refresh();
        final ServiceBusProperties properties = context.getBean(ServiceBusProperties.class);

        assertThat(properties.getConnectionString()).isEqualTo(Constants.CONNECTION_STRING);
        assertThat(properties.getQueueName()).isEqualTo(Constants.QUEUE_NAME);
        assertThat(properties.getQueueReceiveMode()).isEqualTo(Constants.QUEUE_RECEIVE_MODE);
        assertThat(properties.getTopicName()).isEqualTo(Constants.TOPIC_NAME);
        assertThat(properties.getSubscriptionName()).isEqualTo(Constants.SUBSCRIPTION_NAME);
        assertThat(properties.getSubscriptionReceiveMode()).isEqualTo(Constants.SUBSCRIPTION_RECEIVE_MODE);
    }

    @Configuration
    @EnableConfigurationProperties(ServiceBusProperties.class)
    static class Config {
    }
}
