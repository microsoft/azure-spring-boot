[![Travis CI](https://travis-ci.org/Microsoft/azure-spring-boot.svg?branch=master)](https://travis-ci.org/Microsoft/azure-spring-boot)
[![AppVeyor](https://ci.appveyor.com/api/projects/status/af0qeprdv3g9ox07/branch/master?svg=true)](https://ci.appveyor.com/project/yungez/azure-spring-boot)
[![codecov](https://codecov.io/gh/Microsoft/azure-spring-boot/branch/master/graph/badge.svg)](https://codecov.io/gh/Microsoft/azure-spring-boot)
[![MIT License](http://img.shields.io/badge/license-MIT-green.svg) ](https://github.com/Microsoft/azure-spring-boot/blob/master/LICENSE)
[![Gitter](https://badges.gitter.im/Microsoft/spring-on-azure.svg)](https://gitter.im/Microsoft/spring-on-azure)

# Azure Spring Boot

### Introduction

This repo is for Spring Boot Starters of Azure services. It helps Spring Boot developers to adopt Azure services.

### Support Spring Boot
This repository supports both Spring Boot 1.5.x and 2.0.x. Please read [this document](https://github.com/Microsoft/azure-spring-boot/wiki/Spring-Boot-dependency-version-management) for branch mapping.

### Prerequisites
- JDK 1.8 and above
- [Maven](http://maven.apache.org/) 3.0 and above

### Usage

Below starters are available. You can find them in [Maven Central Repository](https://search.maven.org/).
The first three starters are also available in [Spring Initializr](http://start.spring.io/). 

Starter Name | Version
---|---
[azure-active-directory-spring-boot-starter](azure-spring-boot-starters/azure-active-directory-spring-boot-starter/README.md) | [![Maven Central](https://img.shields.io/maven-central/v/com.microsoft.azure/azure-active-directory-spring-boot-starter.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.microsoft.azure%22%20AND%20a%3A%22azure-active-directory-spring-boot-starter%22)
[azure-storage-spring-boot-starter](azure-spring-boot-starters/azure-storage-spring-boot-starter/README.md) | [![Maven Central](https://img.shields.io/maven-central/v/com.microsoft.azure/azure-storage-spring-boot-starter.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.microsoft.azure%22%20AND%20a%3A%22azure-storage-spring-boot-starter%22)
[azure-keyvault-secrets-spring-boot-starter](azure-spring-boot-starters/azure-keyvault-secrets-spring-boot-starter/README.md) | [![Maven Central](https://img.shields.io/maven-central/v/com.microsoft.azure/azure-keyvault-secrets-spring-boot-starter.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.microsoft.azure%22%20AND%20a%3A%22azure-keyvault-secrets-spring-boot-starter%22)
[azure-cosmosdb-spring-boot-starter](azure-spring-boot-starters/azure-cosmosdb-spring-boot-starter/README.md) | [![Maven Central](https://img.shields.io/maven-central/v/com.microsoft.azure/azure-documentdb-spring-boot-starter.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.microsoft.azure%22%20AND%20a%3A%22azure-documentdb-spring-boot-starter%22)
[azure-mediaservices-spring-boot-starter](azure-spring-boot-starters/azure-mediaservices-spring-boot-starter/README.md) | [![Maven Central](https://img.shields.io/maven-central/v/com.microsoft.azure/azure-mediaservices-spring-boot-starter.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.microsoft.azure%22%20AND%20a%3A%22azure-mediaservices-spring-boot-starter%22)
[azure-servicebus-spring-boot-starter](azure-spring-boot-starters/azure-servicebus-spring-boot-starter/README.md) | [![Maven Central](https://img.shields.io/maven-central/v/com.microsoft.azure/azure-servicebus-spring-boot-starter.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.microsoft.azure%22%20AND%20a%3A%22azure-servicebus-spring-boot-starter%22)
[spring-data-gremlin-boot-starter](azure-spring-boot-starters/spring-data-gremlin-boot-starter) | [![Maven Central](https://img.shields.io/maven-central/v/com.microsoft.azure/spring-data-gremlin-boot-starter.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.microsoft.azure%22%20AND%20a%3A%22spring-data-gremlin-boot-starter%22)
[azure-spring-boot-metrics-starter](azure-spring-boot-starters/azure-spring-boot-metrics-starter/README.md) | [![Maven Central](https://img.shields.io/maven-central/v/com.microsoft.azure/azure-spring-boot-metrics-starter.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.microsoft.azure%22%20AND%20a%3A%22azure-spring-boot-metrics-starter%22)



### How to Build and Contribute
This project welcomes contributions and suggestions.  Most contributions require you to agree to a Contributor License Agreement (CLA) declaring that you have the right to, and actually do, grant us the rights to use your contribution. For details, visit https://cla.microsoft.com.

Please follow [instructions here](./HowToContribute.md) to build from source or contribute.

### Other articles
You could check below articles to learn more on usage of specific starters.

[How to use the Spring Boot Starter with Azure Cosmos DB DocumentDB API](https://docs.microsoft.com/en-us/azure/cosmos-db/documentdb-java-spring-boot-starter-with-cosmos-db)

### Filing Issues

If you encounter any bug, please file an issue [here](https://github.com/Microsoft/azure-spring-boot/issues/new).

To suggest a new feature or changes that could be made, file an issue the same way you would for a bug.

You can participate community driven [![Gitter](https://badges.gitter.im/Microsoft/spring-on-azure.svg)](https://gitter.im/Microsoft/spring-on-azure)

### Pull Requests

Pull requests are welcome. To open your own pull request, click [here](https://github.com/Microsoft/azure-spring-boot/compare). When creating a pull request, make sure you are pointing to the fork and branch that your changes were made in.

### Code of Conduct

This project has adopted the [Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/). For more information see the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/) or contact [opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or comments.
