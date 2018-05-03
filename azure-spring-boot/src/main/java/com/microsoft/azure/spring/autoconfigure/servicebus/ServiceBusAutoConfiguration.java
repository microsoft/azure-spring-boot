/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.autoconfigure.servicebus;

import com.microsoft.azure.servicebus.QueueClient;
import com.microsoft.azure.servicebus.SubscriptionClient;
import com.microsoft.azure.servicebus.TopicClient;
import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder;
import com.microsoft.azure.servicebus.primitives.ServiceBusException;
import com.microsoft.azure.telemetry.TelemetryData;
import com.microsoft.azure.telemetry.TelemetryProxy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.ClassUtils;

import java.util.HashMap;

@Lazy
@Configuration
@EnableConfigurationProperties(ServiceBusProperties.class)
@ConditionalOnProperty(prefix = "azure.servicebus", value = "connection-string")
public class ServiceBusAutoConfiguration {

    private final ServiceBusProperties properties;
    private final TelemetryProxy telemetryProxy;

    public ServiceBusAutoConfiguration(ServiceBusProperties properties) {
        this.properties = properties;
        this.telemetryProxy = new TelemetryProxy(properties.isAllowTelemetry());
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "azure.servicebus", value = {"queue-name", "queue-receive-mode"})
    public QueueClient queueClient() throws InterruptedException, ServiceBusException {
        trackCustomEvent();
        return new QueueClient(new ConnectionStringBuilder(properties.getConnectionString(),
                properties.getQueueName()), properties.getQueueReceiveMode());
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "azure.servicebus", value = "topic-name")
    public TopicClient topicClient() throws InterruptedException, ServiceBusException {
        trackCustomEvent();
        return new TopicClient(new ConnectionStringBuilder(properties.getConnectionString(),
                properties.getTopicName()));
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "azure.servicebus",
            value = {"topic-name", "subscription-name", "subscription-receive-mode"})
    public SubscriptionClient subscriptionClient() throws ServiceBusException, InterruptedException {
        trackCustomEvent();
        return new SubscriptionClient(new ConnectionStringBuilder(properties.getConnectionString(),
                properties.getTopicName() + "/subscriptions/" + properties.getSubscriptionName()),
                properties.getSubscriptionReceiveMode());
    }

    private void trackCustomEvent() {
        final HashMap<String, String> customTelemetryProperties = new HashMap<>();
        final String[] packageNames = this.getClass().getPackage().getName().split("\\.");

        if (packageNames.length > 1) {
            customTelemetryProperties.put(TelemetryData.SERVICE_NAME, packageNames[packageNames.length - 1]);
        }
        telemetryProxy.trackEvent(ClassUtils.getUserClass(this.getClass()).getSimpleName(), customTelemetryProperties);
    }
}
