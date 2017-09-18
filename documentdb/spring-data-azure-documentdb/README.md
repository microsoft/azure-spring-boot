## Spring Data for Azure Cosmos DB DocumentDB API
[Azure Cosmos DB](https://docs.microsoft.com/en-us/azure/cosmos-db/introduction) is a globally-distributed database service that allows developers to work with data using a variety of standard APIs, such as DocumentDB, MongoDB, Graph, and Table APIs. Azure DocumentDB Spring Data provides initial Spring Data support for [Azure Cosmos DB Document API](https://docs.microsoft.com/en-us/azure/cosmos-db/documentdb-introduction) based on Spring Data framework, the other 3 APIs are not supported in this package. Key functionalities supported so far include save, delete and find. More features will coming soon.

## Sample Code
Please refer to [sample project here](../spring-data-azure-documentdb-sample).

## Feature List
- Spring Data CRUDRepository basic CRUD functionality
    - save
    - update
    - findAll
    - findOne by Id
    - deleteAll
    - delete by Id
    - delete entity
- Spring Data [@Id](https://github.com/spring-projects/spring-data-commons/blob/db62390de90c93a78743c97cc2cc9ccd964994a5/src/main/java/org/springframework/data/annotation/Id.java) annotation.
  There're 2 ways to map a field in domain class to `id` of Azure Cosmos DB document.
  - annotate a field in domain class with `@Id`, this field will be mapped to document `id` in Cosmos DB. 
  - set name of this field to `id`, this field will be mapped to document `id` in Cosmos DB.
- Custom collection Name.
  By default, collection name will be class name of user domain class. To customize it, add annoataion `@Document(collection="myCustomCollectionName")` to your domain class, that's all.
- Supports [Azure Cosmos DB partition](https://docs.microsoft.com/en-us/azure/cosmos-db/partition-data). To specify a field of your domain class to be partition key field, just annotate it with `@PartitionKey`. When you do CRUD operation, pls specify your partition value. For more sample on partition CRUD, pls refer to [test here](./test/java/com/microsoft/azure/spring/data/documentdb/repository/AddressRepositoryIT.java)
  
## Quick Start

### Add the dependency
`spring-data-azure-documentdb` is published on Maven Central Repository.  
If you are using Maven, add the following dependency.  

```xml
<dependency>
    <groupId>com.microsoft.azure</groupId>
    <artifactId>spring-data-azure-documentdb</artifactId>
    <version>0.1.7</version>
</dependency>
```

### Setup Configuration
Setup Azure Cosmos DB DocumentDB configuration class. Enabling Spring Data Azure DocumentDB repository support is auto-configured.

```
@Configuration
public class AppConfiguration extends AbstractDocumentDbConfiguration {

    @Value("${azure.documentdb.uri}")
    private String uri;

    @Value("${azure.documentdb.key}")
    private String key;

    @Value("${azure.documentdb.database}")
    private String dbName;

    public DocumentClient documentClient() {
        return new DocumentClient(uri, key, ConnectionPolicy.GetDefault(), ConsistencyLevel.Session);
    }

    public String getDatabase() {
        return dbName;
    }
}
```
By default, `@EnableDocumentDbRepositories` will scan the current package for any interfaces that extend one of Spring Data's repository interfaces. Using it to annotate your Configuration class to scan a different root package by type if your project layout has multiple projects and it's not finding your repositories.
```
@Configuration
@EnableDocumentDbRepositories(basePackageClass=UserRepository.class)
public class AppConfiguration extends AbstractDocumentDbConfiguration {
    // configuration code
}
```


### Define en entity
Define a simple entity as Document in DocumentDB.

```
@Document(collection = "mycollection")
public class User {
    private String id;
    private String firstName;
    private String lastName;
 
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
`id` field will be used as document id in Azure DocumentDB. If you want use another field like `emailAddress` as document `id`, just annotate that field with `@Id` annotation.

Annotation `@Document(collection="mycollection")` is used to specify collection name of your document in Azure Cosmos DB.


```
@Document(collection = "mycollection")
public class User {
    @Id
    private String emailAddress;

    ...
}
```

### Create repositories
Extends DocumentDbRepository interface, which provides Spring Data repository support.

```
import com.microsoft.azure.spring.data.documentdb.repository.DocumentDbRepository;
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
Autowired UserRepository interface, then can do save, delete and find operations. Azure Cosmos DB DocumentDB Spring Data uses the DocumentTemplate to execute the queries behind *find*, *save* methods. You can use the template yourself for more complex queries.

## Further info
If you'd like to save effort of configuration, you could directly use Azure Cosmos DB DocumentDB API Spring boot starter at [here](../azure-documentdb-spring-boot-starter).
