## Overview
This sample project demonstrates how to use Azure SQL Server AlwaysEncrypted via Spring Boot Starter `azure-sqlserver-spring-boot-starter` to store data in and retrieve data from Azure SQL Server with AlwaysEncrypted enabled.

## Prerequisites

* An Azure subscription; if you don't already have an Azure subscription, you can activate your [MSDN subscriber benefits](https://azure.microsoft.com/en-us/pricing/member-offers/msdn-benefits-details/) or sign up for a [free Azure account](https://azure.microsoft.com/en-us/free/).

* A [Java Development Kit (JDK)](http://www.oracle.com/technetwork/java/javase/downloads/), version 1.8.

* [Apache Maven](http://maven.apache.org/), version 3.0 or later.

## Quick Start

### Create an Azure SQL DB on Azure

1. Go to [Azure portal](https://portal.azure.com/) and click +New .
2. Click Databases, and then click Azure SQL DB to create your database.
3. Navigate to the database you have created, and click copy JDBC Connection String.
4. Create Azure KeyVault and Service Principal with access to the keys in the Vault
                                                                                                                                  
### Config the sample

1. Navigate to `src/main/resources` and open `application.properties`.
2. replace below properties in `application.properties` with information of your database.

  ```properties
 spring.datasource.jdbc-url=jdbc:sqlserver://<server>.database.windows.net:1433;database=<db>;Encrypt=true;TrustServerCertificate=false;HostNameInCertificate=*.database.windows.net;loginTimeout=30
 spring.datasource.username=
 spring.datasource.password=
 ```
3. To enable always enctyption set the following

```properties
 spring.datasource.always-encrypted.enabled=true
 spring.datasource.always-encrypted.keyvault.client-id=
 spring.datasource.always-encrypted.keyvault.client-secret=
```


### Run the sample

1. Change directory to folder `azure-sqlserver-spring-boot-sample`.
2. Run below commands. 
 
   - Use Maven 

     ```
     mvn package
     java -jar target/azure-sqlserver-spring-boot-sample-0.0.1-SNAPSHOT.jar
     ```

   - Use Gradle 
   
     ```
     gradle bootRepackage
     java -jar build/libs/azure-sqlserver-spring-boot-sample-0.0.1-SNAPSHOT.jar
     ```


