## Usage

### Add the dependency

`azure-storage-spring-boot-starter` is published on Maven Central Repository.  
If you are using Maven, add the following dependency.  

```xml
<dependency>
    <groupId>com.microsoft.azure</groupId>
    <artifactId>azure-storage-spring-boot-starter</artifactId>
    <version>0.2.3</version>
</dependency>
```

### Add the property setting
This starter supports 2 ways of authentication.
1. Connection string.   
   Open `application.properties` file and add below property with your Azure Storage connection string.

   ```
   azure.storage.connection-string=DefaultEndpointsProtocol=[http|https];AccountName=myAccountName;AccountKey=myAccountKey
   ```

2. [Shared access signature](https://docs.microsoft.com/en-us/azure/storage/common/storage-dotnet-shared-access-signature-part-1).  
   Open `application.properties` file and add below property with your Azure Storage connection string.

   ```
   azure.storage.shared-access-signature=?sv=signedversion&ss=signed_services&srt=signed_resource_types&sp=signed_permission&se=start_date&st=effective_date&spr=https&sig=signature_string
   azure.account-name=<your_storage_account_name>
   ```
   Please note shared access signature here should be [account level](https://docs.microsoft.com/en-us/rest/api/storageservices/constructing-an-account-sas).

### Add auto-wiring code

Add below alike code to auto-wire the `CloudStorageAccount` object. Then you can use it to create the client, such as `CloudBlobClient`. For details usage, please reference this [document](https://docs.microsoft.com/en-us/azure/storage/storage-java-how-to-use-blob-storage).

```
@Autowired
private CloudStorageAccount storageAccount;
```

