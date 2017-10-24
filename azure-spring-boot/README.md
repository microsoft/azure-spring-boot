# Azure Spring Boot Support

## Azure Spring Boot AutoConfigure
This package provides auto-configuration for below Azure Services:
- Azure Active Directory
- Cosmos DB DocumentDB API
- Key Vault
- Media Service
- Service Bus
- Storage

Taking Service Bus for example, if you want to use this service in your application, add this dependency and Service Bus library module to your pom file. Or simply use the [Service Bus Starter](../azure-spring-boot-starters/azure-servicebus-spring-boot-starter).

## Azure Cloud Foundry Service
This module also provides the ability to automatically inject credentials from Cloud Foundry into your
applications consuming Azure services. It does this by reading the VCAP_SERVICES environment
variable and setting the appropriate properties used by auto-configuration code.

For details, please see sample code in the [azure-cloud-foundry-service-sample](../azure-spring-boot-samples/azure-cloud-foundry-service-sample) 