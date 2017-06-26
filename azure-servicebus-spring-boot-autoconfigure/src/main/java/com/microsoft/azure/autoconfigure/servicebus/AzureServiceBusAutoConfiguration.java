/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
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
@EnableConfigurationProperties(AzureServiceBusProperties.class)
public class AzureServiceBusAutoConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(AzureServiceBusAutoConfiguration.class);

    private final AzureServiceBusProperties properties;

    public AzureServiceBusAutoConfiguration(AzureServiceBusProperties properties) {
        this.properties = properties;
    }

    @Bean
    @Scope("prototype")
    @ConditionalOnMissingBean
    public QueueClient queueClient() throws InterruptedException, ServiceBusException {
        if (properties.getQueueConnectionString() != null ){
            return new QueueClient(new ConnectionStringBuilder(properties.getQueueConnectionString()),
                    properties.getQueueReceiveMode());
        }

        return null;
    }
}
