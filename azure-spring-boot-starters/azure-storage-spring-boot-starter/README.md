## Usage

### Add the dependency

`azure-storage-spring-boot-starter` is published on Maven Central Repository.  
If you are using Maven, add the following dependency.  

```xml
<dependency>
    <groupId>com.microsoft.azure</groupId>
    <artifactId>azure-storage-spring-boot-starter</artifactId>
    <version>2.2.0</version>
</dependency>
```

### Add the property setting

Open `application.properties` file and add below property with your Azure Storage connection string.

```
azure.storage.account-name=put-your-azure-storage-account-name-here
azure.storage.account-key=put-your-azure-storage-account-key-here
azure.storage.container-name=put-your-azure-storage-container-name-here
```

With above configuration, a `ServiceURL` and a `ContainerURL` bean will be created.
`azure.storage.container-name` is optional, you can also create `ContainerURL` from `ServiceURL` by `ServiceURL#createContainerURL(String containerName)`.

### Use storage emulators for non production environments

If you intend to use [Azure Storage Emulator](https://docs.microsoft.com/en-us/azure/storage/common/storage-use-azurite) or [Azurite](https://github.com/Azure/Azurite) in non production environment, you can enable the emulator use and specify the blob service endpoint.
Open 'application-dev.properties' file and add below property taken from your emulator configurations.

```
azure.storage.account-name=put-your-emulator-storage-account-name-here
azure.storage.account-key=put-your-emulator-storage-account-key-here
azure.storage.container-name=put-your-emulator-storage-container-name-here
azure.storage.use-emulator=true
azure.storage.emulator-blob-host=put-your-emulator-blob-service-host-here
``` 

### Add auto-wiring code

Add below alike code to auto-wire the `ServiceURL` bean and `ContainerURL` bean. For details usage, please reference this [document](https://docs.microsoft.com/en-us/azure/storage/blobs/storage-quickstart-blobs-java-v10#upload-blobs-to-the-container).

```
@Autowired
private ServiceURL serviceURL;

@Autowired
private ContainerURL containerURL;
```

### Enable HTTPs

It's possible to configure Azure to allow only HTTPs connections. By default, the library uses HTTP. If you want to use HTTPs, just add the following configuration in `application.properties`.
```
azure.storage.enable-https=true
```

### Allow telemetry
Microsoft would like to collect data about how users use this Spring boot starter. Microsoft uses this information to improve our tooling experience. Participation is voluntary. If you don't want to participate, just simply disable it by setting below configuration in `application.properties`.
```
azure.storage.allow-telemetry=false
```
When telemetry is enabled, an HTTP request will be sent to URL `https://dc.services.visualstudio.com/v2/track`. So please make sure it's not blocked by your firewall.  
Find more information about Azure Service Privacy Statement, please check [Microsoft Online Services Privacy Statement](https://www.microsoft.com/en-us/privacystatement/OnlineServices/Default.aspx). 

