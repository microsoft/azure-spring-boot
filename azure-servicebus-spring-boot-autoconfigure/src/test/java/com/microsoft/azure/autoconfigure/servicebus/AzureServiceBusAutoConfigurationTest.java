/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */
package com.microsoft.azure.autoconfigure.servicebus;

import com.microsoft.azure.servicebus.QueueClient;
import com.microsoft.azure.servicebus.ReceiveMode;
import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class AzureServiceBusAutoConfigurationTest {
    private static final String DUMMY_QUEUE_CONNECTION_STRING = "dummy queue connection string";
    private static final ReceiveMode QUEUE_RECEIVE_MODE = ReceiveMode.PeekLock;

    @Test
    public void returnNullQueueClientIfNeitherConnectionStingNorReceiveModeSet() {
        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(AzureServiceBusAutoConfiguration.class);
        context.refresh();

        QueueClient queueClient = context.getBean(QueueClient.class);
        assertThat(queueClient).isNull();
    }

    @Test
    public void returnNullQueueClientIfNoReceiveModeSet() {
        System.setProperty("azure.servicebus.queue-connection-string", DUMMY_QUEUE_CONNECTION_STRING);

        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(AzureServiceBusAutoConfiguration.class);
        context.refresh();

        QueueClient queueClient = context.getBean(QueueClient.class);
        assertThat(queueClient).isNull();
    }

    @Test
    public void returnNullQueueClientIfNoConnectionStringSet() {
        System.setProperty("azure.servicebus.queue-receive-mode", QUEUE_RECEIVE_MODE.name());

        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(AzureServiceBusAutoConfiguration.class);
        context.refresh();

        QueueClient queueClient = context.getBean(QueueClient.class);
        assertThat(queueClient).isNull();
    }

    @Test
    public void cannotAutowireQueueClientWithInvalidConnectionString() {
        System.setProperty("azure.servicebus.queue-connection-string", DUMMY_QUEUE_CONNECTION_STRING);
        System.setProperty("azure.servicebus.queue-receive-mode", QUEUE_RECEIVE_MODE.name());

        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(AzureServiceBusAutoConfiguration.class);
        context.refresh();

        QueueClient queueClient = null;

        try {
            queueClient = context.getBean(QueueClient.class);
        } catch (Exception e) {
            assertThat(e.getMessage()).contains("IllegalConnectionStringFormatException: Connection String cannot be parsed");
            assertThat(e).isExactlyInstanceOf(BeanCreationException.class);
        }

        assertThat(queueClient).isNull();
    }
}