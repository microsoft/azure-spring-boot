/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.autoconfigure.servicebus;

import com.microsoft.azure.servicebus.QueueClient;
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
        if (properties.getQueueConnectionString() != null && properties.getQueueReceiveMode() != null) {
            return new QueueClient(new ConnectionStringBuilder(properties.getQueueConnectionString()),
                    properties.getQueueReceiveMode());
        }

        if (properties.getQueueConnectionString() == null) {
            LOG.error("Property azure.servicebus.queue-connection-string is not set.");
        }

        if (properties.getQueueReceiveMode() == null) {
            LOG.error("Property azure.servicebus.queue-receive-mode is not set.");
        }

        return null;
    }
}
