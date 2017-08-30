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
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@EnableConfigurationProperties(ServiceBusProperties.class)
public class ServiceBusAutoConfiguration {

    private final ServiceBusProperties properties;

    public ServiceBusAutoConfiguration(ServiceBusProperties properties) {
        this.properties = properties;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "azure.servicebus", value = {"queue-name", "queue-receive-mode"})
    public QueueClient queueClient() throws InterruptedException, ServiceBusException {
        return new QueueClient(new ConnectionStringBuilder(properties.getConnectionString(),
                properties.getQueueName()), properties.getQueueReceiveMode());
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "azure.servicebus", value = "topic-name")
    public TopicClient topicClient() throws InterruptedException, ServiceBusException {
        return new TopicClient(new ConnectionStringBuilder(properties.getConnectionString(),
                properties.getTopicName()));
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "azure.servicebus",
            value = {"topic-name", "subscription-name", "subscription-receive-mode"})
    public SubscriptionClient subscriptionClient() throws ServiceBusException, InterruptedException {
        return new SubscriptionClient(new ConnectionStringBuilder(properties.getConnectionString(),
                properties.getTopicName() + "/subscriptions/" + properties.getSubscriptionName()),
                properties.getSubscriptionReceiveMode());
    }
}
