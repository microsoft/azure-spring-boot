## Usage

### Add the dependency

"azure-iothub-spring-boot-starter" is published on Maven Central Repository.  
If you are using Maven, add the following dependency.  

```xml
<dependency>
    <groupId>com.microsoft.azure</groupId>
    <artifactId>azure-iothub-spring-boot-starter</artifactId>
    <version>0.1.3</version>
</dependency>
```

### Add the property setting

Open `application.properties` file and add below property with your IoT Hub device connection string.

```
azure.iothub.connection-string=Endpoint=Put the connection string for your device here
```

If you want to use another protocol than HTTPS, please specify it as below. 

```
azure.iothub.protocol=put-your-preferred-protocol-here
```

### Add auto-wiring code

You can use the following code to autowire the Azure IoT Hub DeviceClient instance in your Spring Boot application. Please see sample code in the [azure-iothub-spring-boot-starter-sample](../azure-iothub-spring-boot-starter-sample) folder as a reference.

```
@Autowired
private DeviceClient deviceClient;
```



