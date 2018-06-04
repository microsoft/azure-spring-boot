## Usage

### Add the dependency

`azure-storage-spring-boot-starter` is published on Maven Central Repository.  
If you are using Maven, add the following dependency.  

```xml
<dependency>
    <groupId>com.microsoft.azure</groupId>
    <artifactId>azure-storage-spring-boot-starter</artifactId>
    <version>2.0.2</version>
</dependency>
```

### Add the property setting

Open `application.properties` file and add below property with your Azure Storage connection string.

```
azure.storage.connection-string=DefaultEndpointsProtocol=[http|https];AccountName=myAccountName;AccountKey=myAccountKey
```

### Add auto-wiring code

Add below alike code to auto-wire the `CloudStorageAccount` object. Then you can use it to create the client, such as `CloudBlobClient`. For details usage, please reference this [document](https://docs.microsoft.com/en-us/azure/storage/storage-java-how-to-use-blob-storage).

```
@Autowired
private CloudStorageAccount storageAccount;
```

### Allow telemetry
Microsoft would like to collect data about how users use this Spring boot starter. Microsoft uses this information to improve our tooling experience. Participation is voluntary. If you don't want to participate, just simply disable it by setting below configuration in `application.properties`.
```
azure.storage.allow-telemetry=false
```
Find more information about Azure Service Privacy Statement, please check [Microsoft Online Services Privacy Statement](https://www.microsoft.com/en-us/privacystatement/OnlineServices/Default.aspx). 

