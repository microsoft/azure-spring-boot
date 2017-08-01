## Overview
This sample project demonstrates how to use Azure Storage via Spring Boot Starter `azure-storage-spring-boot-starter`. 

## Prerequisites

* An Azure subscription; if you don't already have an Azure subscription, you can activate your [MSDN subscriber benefits](https://azure.microsoft.com/en-us/pricing/member-offers/msdn-benefits-details/) or sign up for a [free Azure account](https://azure.microsoft.com/en-us/free/).

* A [Java Development Kit (JDK)](http://www.oracle.com/technetwork/java/javase/downloads/), version 1.8.

* [Apache Maven](http://maven.apache.org/), version 3.0 or later.

## Quick Start

### Create storage account on Azure

1. Go to [Azure portal](https://portal.azure.com/) and create the account by following this [link](https://docs.microsoft.com/en-us/azure/storage/storage-create-storage-account). 
2. In the `Acces keys` blade, mark down the `CONNECTION STRING`.
                                                                                                                                  
### Config the sample

1. Navigate to `src/main/resources` and open `application.properties`.
2. Fill in the `connection-string`. 

### Run the sample

1. Change directory to folder `azure-storage-spring-boot-starter-sample`.
2. Run below commands. 

```
mvn package
java -jar target/azure-storage-spring-boot-starter-sample-0.0.1-SNAPSHOT.jar
```