/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.autoconfigure.servicebus;

import com.microsoft.azure.servicebus.ReceiveMode;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("azure.servicebus")
public class ServiceBusProperties {
    private String queueConnectionString;
    private ReceiveMode queueReceiveMode;

    public String getQueueConnectionString() {
        return queueConnectionString;
    }

    public void setQueueConnectionString(String queueConnectionString) {
        this.queueConnectionString = queueConnectionString;
    }

    public ReceiveMode getQueueReceiveMode() {
        return queueReceiveMode;
    }

    public void setQueueReceiveMode(ReceiveMode queueReceiveMode) {
        this.queueReceiveMode = queueReceiveMode;
    }
}

