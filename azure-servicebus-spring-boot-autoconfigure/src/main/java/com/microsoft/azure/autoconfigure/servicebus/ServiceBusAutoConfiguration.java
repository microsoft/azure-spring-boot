/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.autoconfigure.servicebus;

import com.microsoft.azure.servicebus.QueueClient;
import com.microsoft.azure.servicebus.SubscriptionClient;
import com.microsoft.azure.servicebus.TopicClient;
import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder;
import com.microsoft.azure.servicebus.primitives.ServiceBusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@EnableConfigurationProperties(ServiceBusProperties.class)
public class ServiceBusAutoConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceBusAutoConfiguration.class);

    private final ServiceBusProperties properties;

    public ServiceBusAutoConfiguration(ServiceBusProperties properties) {
        this.properties = properties;
    }

    @Bean
    @Scope("prototype")
    @ConditionalOnMissingBean
    public QueueClient queueClient() throws InterruptedException, ServiceBusException {
        if (properties.getConnectionString() != null && properties.getQueueName() != null &&
                properties.getQueueReceiveMode() != null) {
            return new QueueClient(new ConnectionStringBuilder(properties.getConnectionString(),
                    properties.getQueueName()), properties.getQueueReceiveMode());
        }

        if (properties.getConnectionString() == null) {
            LOG.error("Property azure.servicebus.connection-string is not set.");
        }

        if (properties.getQueueName() == null) {
            LOG.error("Property azure.servicebus.queue-name is not set.");
        }

        if (properties.getQueueReceiveMode() == null) {
            LOG.error("Property azure.servicebus.queue-receive-mode is not set.");
        }

        return null;
    }

    @Bean
    @Scope("prototype")
    @ConditionalOnMissingBean
    public TopicClient topicClient() throws InterruptedException, ServiceBusException {
        if (properties.getConnectionString() != null && properties.getTopicName() != null) {
            return new TopicClient(new ConnectionStringBuilder(properties.getConnectionString(),
                    properties.getTopicName()));
        }

        if (properties.getConnectionString() == null) {
            LOG.error("Property azure.servicebus.connection-string is not set.");
        }

        if (properties.getTopicName() == null) {
            LOG.error("Property azure.servicebus.topic-name is not set.");
        }

        return null;
    }

    @Bean
    @Scope("prototype")
    @ConditionalOnMissingBean
    public SubscriptionClient subscriptionClient() throws ServiceBusException, InterruptedException {
        if (properties.getConnectionString() != null && properties.getSubscriptionName() != null
                && properties.getSubscriptionReceiveMode() != null) {
            return new SubscriptionClient(new ConnectionStringBuilder(
                    properties.getConnectionString(),
                    properties.getTopicName() + "/subscriptions/" + properties.getSubscriptionName()),
                    properties.getSubscriptionReceiveMode());
        }

        if (properties.getConnectionString() == null) {
            LOG.error("Property azure.servicebus.connection-string is not set.");
        }

        if (properties.getSubscriptionName() == null) {
            LOG.error("Property azure.servicebus.subscription-name is not set.");
        }

        if (properties.getSubscriptionReceiveMode() == null) {
            LOG.error("Property azure.servicebus.subscription-receive-mode is not set.");
        }

        return null;
    }
}
