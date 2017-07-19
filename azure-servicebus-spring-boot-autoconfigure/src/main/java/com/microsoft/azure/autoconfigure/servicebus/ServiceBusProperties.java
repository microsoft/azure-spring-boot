/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.autoconfigure.servicebus;

import com.microsoft.azure.servicebus.ReceiveMode;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("azure.servicebus")
public class ServiceBusProperties {
    @NotEmpty
    private String connectionString;
    private String queueName;
    private ReceiveMode queueReceiveMode;
    private String topicName;
    private String subscriptionName;
    private ReceiveMode subscriptionReceiveMode;

    public String getConnectionString() {
        return connectionString;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public ReceiveMode getQueueReceiveMode() {
        return queueReceiveMode;
    }

    public void setQueueReceiveMode(ReceiveMode queueReceiveMode) {
        this.queueReceiveMode = queueReceiveMode;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getSubscriptionName() {
        return subscriptionName;
    }

    public void setSubscriptionName(String subscriptionName) {
        this.subscriptionName = subscriptionName;
    }

    public ReceiveMode getSubscriptionReceiveMode() {
        return subscriptionReceiveMode;
    }

    public void setSubscriptionReceiveMode(ReceiveMode subscriptionReceiveMode) {
        this.subscriptionReceiveMode = subscriptionReceiveMode;
    }
}

