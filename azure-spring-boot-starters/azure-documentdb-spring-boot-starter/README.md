## Azure Cosmos DB DocumentDB API Spring boot starter
[Azure Cosmos DB](https://docs.microsoft.com/en-us/azure/cosmos-db/introduction) is a globally-distributed database service that allows developers to work with data using a variety of standard APIs, such as DocumentDB, MongoDB, Graph, and Table APIs. Azure Cosmos DB DocumentDB API Spring boot starter is Spring starter for [Azure Cosmos DB Document API](https://docs.microsoft.com/en-us/azure/cosmos-db/documentdb-introduction) based on Spring Data framework. The other 3 APIs are not supported in this starter. Key functionality supports so far including save, delete and find.

## Feature List
- Spring Data CRUDRepository basic CRUD functionality
    - save
    - findAll
    - findOne by Id
    - deleteAll
    - delete by Id
    - delete entity
- Spring Data [@Id](https://github.com/spring-projects/spring-data-commons/blob/db62390de90c93a78743c97cc2cc9ccd964994a5/src/main/java/org/springframework/data/annotation/Id.java) annotation.
  There're 2 ways to map a field in domain class to `id` of Azure Cosmos DB document.
  - annotate a field in domain class with @Id, this field will be mapped to document `id` in Cosmos DB. 
  - set name of this field to `id`, this field will be mapped to document `id` in Cosmos DB.
    [Note] if both way appplied,    
- Custom collection Name.
   By default, collection name will be class name of user domain class. To customize it, add annoataion `@Document(collection="myCustomCollectionName")` to your domain class, that's all.
- Supports [Azure Cosmos DB partition](https://docs.microsoft.com/en-us/azure/cosmos-db/partition-data). To specify a field of your domain class to be partition key field, just annotate it with `@PartitionKey`. When you do CRUD operation, pls specify your partition value. For more sample on partition CRUD, pls refer to [test here](./test/java/com/microsoft/azure/spring/data/documentdb/repository/AddressRepositoryIT.java)   

## Sample Code
Please refer to [sample project here](../../azure-spring-boot-samples/azure-documentdb-spring-boot-sample).

## Quick Start

### Add the dependency

`azure-documentdb-spring-boot-starter` is published on Maven Central Repository.  
If you are using Maven, add the following dependency.  

```xml
<dependency>
    <groupId>com.microsoft.azure</groupId>
    <artifactId>azure-documentdb-spring-boot-starter</artifactId>
    <version>0.2.0</version>
</dependency>
```

### Add the property setting

Open `application.properties` file and add below properties with your Cosmos DB credentials.

```
azure.documentdb.uri=your-documentdb-uri
azure.documentdb.key=your-documentdb-key
azure.documentdb.database=your-documentdb-databasename
```

Property `azure.documentdb.consistency-level` is also supported.

### Define en entity
Define a simple entity as Document in DocumentDB.

```
@Document(collection = "mycollection")
public class User {
    private String id;
    private String firstName;
    private String lastName;
    // if emailAddress is mapped to id, then
    // @Id
    // private String emailAddress
 
    ... // setters and getters

    public User(String id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return String.format("User: %s %s, %s", firstName, lastName);
    }
}
```
`id` field will be used as document `id` in Azure Cosmos DB. Or you can annotate any field with `@Id` to map it to document `id`.

Annotation `@Document(collection="mycollection")` is used to specify collection name of your document in Azure Cosmos DB.

### Create repositories
Extends DocumentDbRepository interface, which provides Spring Data repository support.

```
import com.microsoft.azure.spring.data.cosmosdb.documentdb.repository.DocumentDbRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends DocumentDbRepository<User, String> {
}
```

So far DocumentDbRepository provides basic save, delete and find operations. More operations will be supported later.

### Create an Application class
Here create an application class with all the components
```
@SpringBootApplication
public class SampleApplication implements CommandLineRunner {

    @Autowired
    private UserRepository repository;

    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }

    public void run(String... var1) throws Exception {

        final User testUser = new User("testId", "testFirstName", "testLastName");

        repository.deleteAll();
        repository.save(testUser);

        final User result = repository.findOne(testUser.getId());
        // if emailAddress is mapped to id, then 
        // final User result = respository.findOne(testUser.getEmailAddress());        
    }
}
```
Autowired UserRepository interface, then can do save, delete and find operations.

### Allow telemetry
Microsoft would like to collect data about how users use this Spring boot starter. Microsoft uses this information to improve our tooling experience. Participation is voluntary. If you don't want to participate, just simply disable it by setting below configuration in `application.properties`.
```
azure.documentdb.allow-telemetry=false
```
Find more information about Azure Service Privacy Statement, please check [Microsoft Online Services Privacy Statement](https://www.microsoft.com/en-us/privacystatement/OnlineServices/Default.aspx). 

### Further info

Besides using Azure Cosmos DB DocumentDB API Spring boot starter, you can directly use Azure Cosmos DB DocumentDB API Spring Data package to more complex scenario, detail pls refer to [Spring Data for Azure Cosmos DB DocumentDB API](https://github.com/Microsoft/spring-data-documentdb).


