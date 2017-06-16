## Usage

### Add the dependency

"spring-boot-azure-cosmosdb-autoconfigure" is published on Maven Central Repository.  
If you are using Maven, add the following dependency.  

```xml
<dependency>
    <groupId>com.microsoft.azure</groupId>
    <artifactId>spring-boot-azure-cosmosdb-autoconfigure</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

### Add the property setting

Open `application.properties` file and add below properties with your Cosmos DB credentials.

```
azure.cosmosdb.uri=your-cosmosdb-uri
azure.cosmosdb.key=your-cosmosdb-key
```

Property `azure.cosmosdb.consistency-level` is also supported.

### Add auto-wiring code

Add below alike code to auto-wire the `DocumentClient` object. For more sample code, please check this [tutorial](https://docs.microsoft.com/en-us/azure/cosmos-db/documentdb-java-application).

```
@Autowired
private DocumentClient client;
```

### Further customization

The `DocumentClient` object also accepts `ConnectionPolicy` parameter. You can customize one `ConnectionPolicy` object and add it as a `ConnectionPolicy` bean in your configuration class. This bean will be used when `DocumentClient` object is auto-wired. If such bean not exists, a `ConnectionPolicy` object with default values is used.


