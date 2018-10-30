## Azure SQL DB AlwaysEncrypted Spring Boot Starter

[Azure SQL DB Always Encrypted](https://docs.microsoft.com/en-us/azure/sql-database/sql-database-always-encrypted) feature allows to encrypt data in SQL and store master keys in Azure KeyVault

## Sample Code
Please refer to [sample project here](../../azure-spring-boot-samples/azure-sqlserver-spring-boot-sample).

## Quick Start

### Add the dependency

`azure-sqlserver-spring-boot-starter` is published on Maven Central Repository.
If you are using Maven, add the following dependency.  

```xml
<dependency>
    <groupId>com.microsoft.azure</groupId>
    <artifactId>azure-sqlserver-spring-boot-starter</artifactId>
</dependency>
```

### Add the property settings

Open `application.properties` file and add below properties with your SQL DB credentials.

 ```properties
 spring.datasource.jdbc-url=jdbc:sqlserver://<server>.database.windows.net:1433;database=<db>;Encrypt=true;TrustServerCertificate=false;HostNameInCertificate=*.database.windows.net;loginTimeout=30
 spring.datasource.username=
 spring.datasource.password=
 ```
 To enable always encryption set the following

```properties
 spring.datasource.always-encrypted=true
 spring.datasource.always-encrypted.keyvault.client-id=
 spring.datasource.always-encrypted.keyvault.client-secret=
```


### Allow telemetry
Microsoft would like to collect data about how users use this Spring boot starter. Microsoft uses this information to improve our tooling experience. Participation is voluntary. If you don't want to participate, just simply disable it by setting below configuration in `application.properties`.
```
 spring.datasource.always-encrypted.keyvault.allow-telemetry=false
```
Find more information about Azure Service Privacy Statement, please check [Microsoft Online Services Privacy Statement](https://www.microsoft.com/en-us/privacystatement/OnlineServices/Default.aspx).

