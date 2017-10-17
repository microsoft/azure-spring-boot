# Azure Spring Boot Support

## Azure Spring Boot AutoConfigure
This package provides auto-configuration for below Azure Services:
- Azure Active Directory
- Cosmos DB DocumentDB API
- Key Vault
- Media Service
- Service Bus
- Storage


## Azure Cloud Foundry Service
This module also provides the ability to automatically inject credentials from Cloud Foundry into your
applications consuming Azure services. It does this by reading the VCAP_SERVICES environment
variable and setting the appropriate properties on the azure auto-configuration projects.