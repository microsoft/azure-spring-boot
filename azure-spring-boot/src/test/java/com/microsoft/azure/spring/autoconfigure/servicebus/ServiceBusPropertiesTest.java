/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.servicebus;

import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.validation.BindValidationException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.ObjectError;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class ServiceBusPropertiesTest {
    @Test
    public void canSetQueueProperties() {
        System.setProperty(Constants.CONNECTION_STRING_PROPERTY, Constants.INVALID_CONNECTION_STRING);
        System.setProperty(Constants.QUEUE_NAME_PROPERTY, Constants.QUEUE_NAME);
        System.setProperty(Constants.QUEUE_RECEIVE_MODE_PROPERTY, Constants.QUEUE_RECEIVE_MODE.name());
        System.setProperty(Constants.TOPIC_NAME_PROPERTY, Constants.TOPIC_NAME);
        System.setProperty(Constants.SUBSCRIPTION_NAME_PROPERTY, Constants.SUBSCRIPTION_NAME);
        System.setProperty(Constants.SUBSCRIPTION_RECEIVE_MODE_PROPERTY, Constants.SUBSCRIPTION_RECEIVE_MODE.name());

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(Config.class);
            context.refresh();
            final ServiceBusProperties properties = context.getBean(ServiceBusProperties.class);

            assertThat(properties.getConnectionString()).isEqualTo(Constants.INVALID_CONNECTION_STRING);
            assertThat(properties.getQueueName()).isEqualTo(Constants.QUEUE_NAME);
            assertThat(properties.getQueueReceiveMode()).isEqualTo(Constants.QUEUE_RECEIVE_MODE);
            assertThat(properties.getTopicName()).isEqualTo(Constants.TOPIC_NAME);
            assertThat(properties.getSubscriptionName()).isEqualTo(Constants.SUBSCRIPTION_NAME);
            assertThat(properties.getSubscriptionReceiveMode()).isEqualTo(Constants.SUBSCRIPTION_RECEIVE_MODE);
        }

        System.clearProperty(Constants.CONNECTION_STRING_PROPERTY);
        System.clearProperty(Constants.QUEUE_NAME_PROPERTY);
        System.clearProperty(Constants.QUEUE_RECEIVE_MODE_PROPERTY);
        System.clearProperty(Constants.TOPIC_NAME_PROPERTY);
        System.clearProperty(Constants.SUBSCRIPTION_NAME_PROPERTY);
        System.clearProperty(Constants.SUBSCRIPTION_RECEIVE_MODE_PROPERTY);
    }

    @Test
    public void connectionStringIsNull() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(Config.class);

            Exception exception = null;
            try {
                context.refresh();
            } catch (Exception e) {
                exception = e;
            }

            assertThat(exception).isNotNull();
            assertThat(exception).isExactlyInstanceOf(BeanCreationException.class);

            final BindValidationException bindValidationException =
                    (BindValidationException) exception.getCause().getCause();
            final List<ObjectError> errors = bindValidationException.getValidationErrors().getAllErrors();
            assertThat(errors.size()).isEqualTo(1);

            assertThat(errors.get(0).toString()).contains(
                    "Field error in object 'azure.servicebus' on field 'connectionString': " +
                            "rejected value [null];");
        }
    }

    @Configuration
    @EnableConfigurationProperties(ServiceBusProperties.class)
    static class Config {
    }
}
