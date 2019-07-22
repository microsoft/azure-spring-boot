## Usage

This code [sample](../../azure-spring-boot-samples/azure-servicebus-jms-spring-boot-sample/) demonstrates how to use Spring JMS for Azure Service Bus.

Running this sample will be charged by Azure. You can check the usage and bill at this [link](https://azure.microsoft.com/en-us/account/).

### Add the dependency

`azure-servicebus-jms-spring-boot-starter` is published on Maven Central Repository.  
Add the following dependency to your project:

*Maven*
```xml
<dependency>
    <groupId>com.microsoft.azure</groupId>
    <artifactId>azure-servicebus-jms-spring-boot-starter</artifactId>
    <version>2.0.4</version>
</dependency>
```

*Gradle*<br>
```compile 'com.microsoft.azure:azure-servicebus-jms-spring-boot-starter:2.0.4'```

### Add the property setting

1. Create Azure Service Bus namespace, queue and topic. Please see [how to create](https://docs.microsoft.com/en-us/azure/service-bus-messaging/service-bus-create-namespace-portal). 

2. Update [application.properties](../../azure-spring-boot-samples/azure-servicebus-jms-spring-boot-sample/src/main/resources/application.properties)

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

1. Update `QUEUE_NAME` in [QueueSendController](../../azure-spring-boot-samples/azure-servicebus-jms-spring-boot-sample/src/main/java/sample/jms/QueueSendController.java) and [QueueReceiveController](../../azure-spring-boot-samples/azure-servicebus-jms-spring-boot-sample/src/main/java/sample/jms/QueueReceiveController.java),
`TOPIC_NAME` in [TopicSendController](../../azure-spring-boot-samples/azure-servicebus-jms-spring-boot-sample/src/main/java/sample/jms/TopicSendController.java) and [TopicReceiveController](../../azure-spring-boot-samples/azure-servicebus-jms-spring-boot-sample/src/main/java/sample/jms/TopicReceiveController.java),
and `SUBSCRIPTION_NAME` in [TopicReceiveController](../../azure-spring-boot-samples/azure-servicebus-jms-spring-boot-sample/src/main/java/sample/jms/TopicReceiveController.java).

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

### Allow telemetry
Microsoft would like to collect data about how users use this Spring boot starter. Microsoft uses this information to improve our tooling experience. Participation is voluntary. If you don't want to participate, just simply disable it by setting below configuration in `application.properties`.
```
azure.servicebus.allow-telemetry=false
```
Find more information about Azure Service Privacy Statement, please check [Microsoft Online Services Privacy Statement](https://www.microsoft.com/en-us/privacystatement/OnlineServices/Default.aspx). 




