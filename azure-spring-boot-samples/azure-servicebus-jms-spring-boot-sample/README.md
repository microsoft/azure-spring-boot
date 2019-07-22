## Overview

This sample project demonstrates how to use Spring JMS for Azure Service Bus via Spring Boot Starter `azure-servicebus-jms-spring-boot-starter`. 

Running this sample will be charged by Azure. You can check the usage and bill at this [link](https://azure.microsoft.com/en-us/account/).

## Prerequisites

* An Azure subscription; if you don't already have an Azure subscription, you can activate your [MSDN subscriber benefits](https://azure.microsoft.com/en-us/pricing/member-offers/msdn-benefits-details/) or sign up for a [free Azure account](https://azure.microsoft.com/en-us/free/).

* A [Java Development Kit (JDK)](http://www.oracle.com/technetwork/java/javase/downloads/), version 1.8.

* [Apache Maven](http://maven.apache.org/), version 3.0 or later.

## Quick Start

### Create Service Bus on Azure

1. Go to [Azure portal](https://portal.azure.com/) and create the service by following this [link](https://docs.microsoft.com/en-us/azure/service-bus-messaging/service-bus-create-namespace-portal). 
2. Update [application.properties](./src/main/resources/application.properties)

```
# Fill service bus namespace connection string copied from portal
azure.servicebus.jms.connection-string=[servicebus-namespace-connection-string]

# Client ID is required if using service bus topic
azure.servicebus.jms.client-id=[client-id]

# Idle Timeout will be set to a default value of 3600000 if you don't set it
azure.servicebus.jms.idle-timeout=[idle-timeout]
```

In addition, you may want to specify your queue name if you want use queue, or you may want to specify your topic name and subscription name if you want use topic.

Please see `How to run` below for details.
                                                                                                                                                                                    
### How to run

1. Update `QUEUE_NAME` in [QueueSendController](./src/main/java/sample/jms/QueueSendController.java) and [QueueReceiveController](./src/main/java/sample/jms/QueueReceiveController.java),
`TOPIC_NAME` in [TopicSendController](./src/main/java/sample/jms/TopicSendController.java) and [TopicReceiveController](./src/main/java/sample/jms/TopicReceiveController.java),
and `SUBSCRIPTION_NAME` in [TopicReceiveController](./src/main/java/sample/jms/TopicReceiveController.java).

2. Run the `mvn clean spring-boot:run` in the root of the code sample to get the app running.

3. Send a POST request to service bus queue.
    ```
    $ curl -X POST localhost:8080/queue?message=hello
    ```
    
4. Verify in your app's logs that a similar message was posted:
    ```
    Sending message
    Received message from queue: hello
    ```

5. Send a POST request to service bus topic.
    ```
    $ curl -X POST localhost:8080/topic?message=hello
    ```

6. Verify in your app's logs that a similar message was posted:
    ```
    Sending message
    Received message from topic: hello
    ```
    
7. Delete the resources on [Azure Portal](http://ms.portal.azure.com/) to avoid extra charges.

### More usage

Please check the following table for reference links of detailed Service Bus usage. 

Type | Reference Link
--- | ---
`Queues` | [https://docs.microsoft.com/en-us/azure/service-bus-messaging/service-bus-java-how-to-use-queues](https://docs.microsoft.com/en-us/azure/service-bus-messaging/service-bus-java-how-to-use-queues)
`Topics` | [https://docs.microsoft.com/en-us/azure/service-bus-messaging/service-bus-java-how-to-use-topics-subscriptions](https://docs.microsoft.com/en-us/azure/service-bus-messaging/service-bus-java-how-to-use-topics-subscriptions)
`Subscriptions` | [https://docs.microsoft.com/en-us/azure/service-bus-messaging/service-bus-java-how-to-use-topics-subscriptions](https://docs.microsoft.com/en-us/azure/service-bus-messaging/service-bus-java-how-to-use-topics-subscriptions)
