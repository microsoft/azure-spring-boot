## Usage

### Add the dependency

"azure-mediaservices-spring-boot-starter" is published on Maven Central Repository.  
If you are using Maven, add the following dependency.  

```xml
<dependency>
    <groupId>com.microsoft.azure</groupId>
    <artifactId>azure-mediaservices-spring-boot-starter</artifactId>
    <version>0.1.3</version>
</dependency>
```

### Add the property setting

Open `application.properties` file and add below properties with your Azure Media Services credentials.

```
azure.mediaservices.account-name=put-your-media-services-account-name-here
azure.mediaservices.account-key=put-your-media-services-account-key-here
```

### Add auto-wiring code

Add below alike code to auto-wire the `MediaContract` object. Then you can use it to upload, encode and set streaming url. For details usage, please reference this [document](https://docs.microsoft.com/en-us/azure/media-services/media-services-java-how-to-use).

```
@Autowired
private MediaContract mediaService;
```

