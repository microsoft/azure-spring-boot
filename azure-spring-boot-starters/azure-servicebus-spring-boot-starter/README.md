## Usage

### Add the dependency

`azure-servicebus-spring-boot-starter` is published on Maven Central Repository.  
If you are using Maven, add the following dependency.  

```xml
<dependency>
    <groupId>com.microsoft.azure</groupId>
    <artifactId>azure-servicebus-spring-boot-starter</artifactId>
    <version>0.1.9</version>
</dependency>
```

### Add the property setting

Open `application.properties` file and add below property with your Service Bus connection string.

```
azure.servicebus.connection-string=Endpoint=myEndpoint;SharedAccessKeyName=mySharedAccessKeyName;SharedAccessKey=mySharedAccessKey
```

If you want to use queue, please specify your created queue name and receive mode as below. 

```
azure.servicebus.queue-name=put-your-queue-name-here
azure.servicebus.queue-receive-mode=peeklock
```

For topic, please specify your created topic name. 

```
azure.servicebus.topic-name=put-your-topic-name-here
```

For subscription, please specify your created subscription name and receive mode.

```
azure.servicebus.subscription-name=put-your-subscription-name-here
azure.servicebus.subscription-receive-mode=peeklock
```

### Add auto-wiring code

You can use the following code to autowire the Azure Service Bus Queue, Topic, and Subscription clients in your Spring Boot application. Please see sample code in the [azure-servicebus-spring-boot-sample](../../azure-spring-boot-samples/azure-servicebus-spring-boot-sample) folder as a reference.

```
@Autowired
private QueueClient queueClient;
```

```
@Autowired
private TopicClient topicClient;
```

```
@Autowired
private SubscriptionClient subscriptionClient;
```


