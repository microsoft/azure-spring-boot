## Overview
Azure DocumentDB Spring Data provides initial Spring Data support for [Azure Cosmos DB Document API](https://docs.microsoft.com/en-us/azure/cosmos-db/documentdb-introduction) based on Spring Data framework. Key functionalities supported so far include save, delete and find. More features will coming soon.

## Sample Code
Pls refer to [sample project here](../spring-data-azure-documentdb-sample).

## Quick Start

### Add the dependency
"spring-data-azure-documentdb" is published on Maven Central Repository.  
If you are using Maven, add the following dependency.  

```xml
<dependency>
    <groupId>com.microsoft.azure</groupId>
    <artifactId>spring-data-azure-documentdb</artifactId>
    <version>0.1.3</version>
</dependency>
```

### Setup Configuration
Setup Azure DocumentDB configuration class. Enabling Spring Data Azure DocumentDB repository support is autoconfigured.

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
By default, @EnableDocumentDbRepositories will scan the current package for any interfaces that extend one of Spring Data's repository interfaces. Using it to annotate your Configuration class to scan a different root package by type if your project layout has multiple projects and it's not finding your repositories.
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
`id` field will be used as document id in Azure DocumentDB. `id` field is must.


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
    }
}
```
Autowired UserRepository interface, then can do save, delete and find operations. Azure DocumentDB Spring Data uses the DocumentTemplate to execute the quries behind *find*, *save* methods. You can use the template yourself for more complex queries.

## Furthur info
If you'd like to save effort of configuration, you could directly use Azure DocumentDB Spring boot starter at [here](../azure-documentdb-spring-boot-start).
