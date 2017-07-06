## Usage

### Add the dependency

"azure-documentdb-spring-boot-autoconfigure" is published on Maven Central Repository.  
If you are using Maven, add the following dependency.  

```xml
<dependency>
    <groupId>com.microsoft.azure</groupId>
    <artifactId>azure-documentdb-spring-boot-autoconfigure</artifactId>
    <version>0.1.3</version>
</dependency>
```

### Add the property setting

Open `application.properties` file and add below properties with your Document DB credentials.

```
azure.documentdb.uri=your-documentdb-uri
azure.documentdb.key=your-documentdb-key
```

Property `azure.documentdb.consistency-level` is also supported.

### Add auto-wiring code

Add below alike code to auto-wire the `DocumentClient` object. For more sample code, please check this [tutorial](https://docs.microsoft.com/en-us/azure/cosmos-db/documentdb-java-application).

```
@Autowired
private DocumentClient client;
```

### Further customization

The `DocumentClient` object also accepts `ConnectionPolicy` parameter. You can customize one `ConnectionPolicy` object and add it as a `ConnectionPolicy` bean in your configuration class. This bean will be used when `DocumentClient` object is auto-wired. If such bean not exists, a `ConnectionPolicy` object with default values is used.



