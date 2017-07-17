/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.autoconfigure.servicebus;

import com.microsoft.azure.servicebus.QueueClient;
import com.microsoft.azure.servicebus.SubscriptionClient;
import com.microsoft.azure.servicebus.TopicClient;
import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class ServiceBusAutoConfigurationTest {
    @Test
    public void returnNullIfNoPropertiesSet() {
        System.setProperty(Constants.CONNECTION_STRING_PROPERTY, Constants.CONNECTION_STRING);

        final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(ServiceBusAutoConfiguration.class);
        context.refresh();

        final QueueClient queueClient = context.getBean(QueueClient.class);
        assertThat(queueClient).isNull();

        final TopicClient topicClient = context.getBean(TopicClient.class);
        assertThat(topicClient).isNull();

        final SubscriptionClient subscriptionClient = context.getBean(SubscriptionClient.class);
        assertThat(subscriptionClient).isNull();

        final String outContentString = outContent.toString();
        assertThat(outContentString).contains("Property azure.servicebus.subscription-name is not set.");
        assertThat(outContentString).contains("Property azure.servicebus.subscription-receive-mode is not set.");
        assertThat(outContentString).contains("Property azure.servicebus.topic-name is not set.");
        assertThat(outContentString).contains("Property azure.servicebus.queue-name is not set.");
        assertThat(outContentString).contains("Property azure.servicebus.queue-receive-mode is not set.");

        System.setErr(null);
        System.clearProperty(Constants.CONNECTION_STRING_PROPERTY);
    }

    @Test
    public void cannotAutowireQueueClientWithInvalidConnectionString() {
        System.setProperty(Constants.CONNECTION_STRING_PROPERTY, Constants.CONNECTION_STRING);
        System.setProperty(Constants.QUEUE_NAME_PROPERTY, Constants.QUEUE_NAME);
        System.setProperty(Constants.QUEUE_RECEIVE_MODE_PROPERTY, Constants.QUEUE_RECEIVE_MODE.name());

        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(ServiceBusAutoConfiguration.class);
        context.refresh();

        QueueClient queueClient = null;

        Exception exception = null;
        try {
            queueClient = context.getBean(QueueClient.class);
        } catch (Exception e) {
            exception = e;
        }

        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).contains(
                "IllegalConnectionStringFormatException: Connection String cannot be parsed");
        assertThat(exception).isExactlyInstanceOf(BeanCreationException.class);

        assertThat(queueClient).isNull();

        System.clearProperty(Constants.CONNECTION_STRING_PROPERTY);
        System.clearProperty(Constants.QUEUE_NAME_PROPERTY);
        System.clearProperty(Constants.QUEUE_RECEIVE_MODE_PROPERTY);
    }

    @Test
    public void cannotAutowireTopicClientWithInvalidConnectionString() {
        System.setProperty(Constants.CONNECTION_STRING_PROPERTY, Constants.CONNECTION_STRING);
        System.setProperty(Constants.TOPIC_NAME_PROPERTY, Constants.TOPIC_NAME);

        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(ServiceBusAutoConfiguration.class);
        context.refresh();

        TopicClient topicClient = null;

        Exception exception = null;
        try {
            topicClient = context.getBean(TopicClient.class);
        } catch (Exception e) {
            exception = e;
        }

        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).contains(
                "IllegalConnectionStringFormatException: Connection String cannot be parsed");
        assertThat(exception).isExactlyInstanceOf(BeanCreationException.class);

        assertThat(topicClient).isNull();

        System.clearProperty(Constants.CONNECTION_STRING_PROPERTY);
        System.clearProperty(Constants.TOPIC_NAME_PROPERTY);
    }

    @Test
    public void cannotAutowireSubscriptionClientWithInvalidConnectionString() {
        System.setProperty(Constants.CONNECTION_STRING_PROPERTY, Constants.CONNECTION_STRING);
        System.setProperty(Constants.TOPIC_NAME_PROPERTY, Constants.TOPIC_NAME);
        System.setProperty(Constants.SUBSCRIPTION_NAME_PROPERTY, Constants.SUBSCRIPTION_NAME);
        System.setProperty(Constants.SUBSCRIPTION_RECEIVE_MODE_PROPERTY, Constants.SUBSCRIPTION_RECEIVE_MODE.name());

        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(ServiceBusAutoConfiguration.class);
        context.refresh();

        SubscriptionClient subscriptionClient = null;

        Exception exception = null;
        try {
            subscriptionClient = context.getBean(SubscriptionClient.class);
        } catch (Exception e) {
            exception = e;
        }

        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).contains(
                "IllegalConnectionStringFormatException: Connection String cannot be parsed");
        assertThat(exception).isExactlyInstanceOf(BeanCreationException.class);

        assertThat(subscriptionClient).isNull();

        System.clearProperty(Constants.CONNECTION_STRING_PROPERTY);
        System.clearProperty(Constants.TOPIC_NAME_PROPERTY);
        System.clearProperty(Constants.SUBSCRIPTION_NAME_PROPERTY);
        System.clearProperty(Constants.SUBSCRIPTION_RECEIVE_MODE_PROPERTY);
    }
}
