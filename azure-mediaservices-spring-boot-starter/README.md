## Usage

### Add the dependency

"azure-media-spring-boot-starter" is published on Maven Central Repository.  
If you are using Maven, add the following dependency.  

```xml
<dependency>
    <groupId>com.microsoft.azure</groupId>
    <artifactId>azure-media-spring-boot-starter</artifactId>
    <version>0.1.3</version>
</dependency>
```

### Add the property setting

Open `application.properties` file and add below property with your Azure Storage connection string.

```
azure.media.mediaServiceUri=https://media.windows.net/API/
azure.media.oAuthUri=https://wamsprodglobal001acs.accesscontrol.windows.net/v2/OAuth2-13
azure.media.clientId=put-your-azure-media-clientId-here
azure.media.clientSecret=put-your-azure-media-secret-here
azure.media.scope=urn:WindowsAzureMediaServices
```

### Add auto-wiring code

Add below alike code to auto-wire the `MediaContract` object. Then you can use it to upload, encode and set streaming url. For details usage, please reference this [document](https://docs.microsoft.com/en-us/azure/media-services/media-services-java-how-to-use).

```
@Autowired
private MediaContract mediaService;
```

