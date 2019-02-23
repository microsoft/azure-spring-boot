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
import com.microsoft.azure.telemetry.TelemetryProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.ClassUtils;

import javax.annotation.PostConstruct;
import java.util.HashMap;

import static com.microsoft.azure.telemetry.TelemetryData.*;
import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;

@Lazy
@Slf4j
@Configuration
@EnableConfigurationProperties(ServiceBusProperties.class)
@ConditionalOnProperty(prefix = "azure.servicebus", value = "connection-string")
public class ServiceBusAutoConfiguration {

    private final ServiceBusProperties properties;
    private final TelemetryProxy telemetryProxy;

    public ServiceBusAutoConfiguration(ServiceBusProperties properties, TelemetryProxy telemetryProxy) {
        this.properties = properties;
        this.telemetryProxy = telemetryProxy;
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

    private String getHashNamespace() {
        final String namespace = properties.getConnectionString()
                .replaceFirst("^.*//", "") // emit head 'Endpoint=sb://'
                .replaceAll("\\..*$", ""); // emit tail '${namespace}.xxx.xxx'

        // Namespace can only be letter, number and hyphen, start with letter, end with letter or number,
        // with length of 6-50.
        if (!namespace.matches("[a-zA-Z][a-zA-Z-0-9]{4,48}[a-zA-Z0-9]")) {
            log.warn("Unexpected name {}, please check if it's valid name or portal name rule changes.", namespace);
        }

        return sha256Hex(namespace);
    }

    @PostConstruct
    private void trackCustomEvent() {
        final HashMap<String, String> events = new HashMap<>();

        events.put(SERVICE_NAME, getClassPackageSimpleName(ServiceBusAutoConfiguration.class));
        events.put(HASHED_NAMESPACE, getHashNamespace());

        telemetryProxy.trackEvent(ClassUtils.getUserClass(this.getClass()).getSimpleName(), events);
    }
}
