## Usage

### Add the dependency

`azure-mediaservices-spring-boot-starter` is published on Maven Central Repository.  
If you are using Maven, add the following dependency.  

```xml
<dependency>
    <groupId>com.microsoft.azure</groupId>
    <artifactId>azure-mediaservices-spring-boot-starter</artifactId>
    <version>0.1.7</version>
</dependency>
```

### Add the property setting

Open `application.properties` file and add below properties with your Azure Media Services credentials.

```
azure.mediaservices.account-name=put-your-media-services-account-name-here
azure.mediaservices.account-key=put-your-media-services-account-key-here
```

# Optional
If you are using network proxy then add below properties to run azure media services behind proxy.

```
azure.mediaservices.proxy-host=put-your-network-proxy-host
azure.mediaservices.proxy-port=put-your-network-proxy-port
azure.mediaservices.proxy-scheme=put-your-network-proxy-scheme
```

### Add auto-wiring code

Add below alike code to auto-wire the `MediaContract` object. Then you can use it to upload, encode and set streaming url. For details usage, please reference this [document](https://docs.microsoft.com/en-us/azure/media-services/media-services-java-how-to-use).

```
@Autowired
private MediaContract mediaService;
```

