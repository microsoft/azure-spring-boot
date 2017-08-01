## Overview
Azure DocumentDB Spring boot starter is Spring starter for [Azure Cosmos DB Document API](https://docs.microsoft.com/en-us/azure/cosmos-db/documentdb-introduction) based on Spring Data framework. Key functionality supports so far including save, delete and find.

## Sample Code
Pls refer to [sample project here](../azure-documentdb-spring-boot-starter-sample).

## Quick Start

### Add the dependency

`azure-documentdb-spring-boot-starter` is published on Maven Central Repository.  
If you are using Maven, add the following dependency.  

```xml
<dependency>
    <groupId>com.microsoft.azure</groupId>
    <artifactId>azure-documentdb-spring-boot-starter</artifactId>
    <version>0.1.4</version>
</dependency>
```

### Add the property setting

Open `application.properties` file and add below properties with your Document DB credentials.

```
azure.documentdb.uri=your-documentdb-uri
azure.documentdb.key=your-documentdb-key
azure.documentdb.database=your-documentdb-databasename
```

Property `azure.documentdb.consistency-level` is also supported.

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
Autowired UserRepository interface, then can do save, delete and find operations.

### Allow telemetry
Microsoft would like to collect data about how users use this Spring boot starter. Microsoft uses this information to improve our tooling experience. Participation is voluntary. If you don't want to participate, just simply disable it by setting below configuration in `application.properties`.
```
azure.documentdb.allow-telemetry=false
```
Find more information about Azure Service Privacy Satement, please check [Microsoft Online Services Privacy Statement](https://www.microsoft.com/en-us/privacystatement/OnlineServices/Default.aspx). 

### Further info

Besides using Azure DocumentDB Spring boot starter, you can directly use Azure DocumentDB Spring Data package to more complex scenario, detail pls refer to [Azure DocumentDB Spring Data](../spring-data-azure-documentdb/README.md).


