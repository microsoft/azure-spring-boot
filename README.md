# Azure Spring Boot

## The Spring modules in this repo are moved to [Azure Java SDK repo](https://github.com/Azure/azure-sdk-for-java/tree/master/sdk/spring).

Spring Cloud Azure 4.x libraries have been GAed and it bring much more consistent experience, check this [reference doc](https://microsoft.github.io/spring-cloud-azure/) for more details.

## We will continue to respond to open issues here, new issues should be reported on [Azure Java SDK repo](https://github.com/Azure/azure-sdk-for-java/).

## Thank you for your patience. We look forward to continuing to work together with you.

## Below are the new locations for Spring modules

|Module name                                   |New module name                                   |Has been deprecated |Description
|----------------------------------------------|--------------------------------------------------|--------------------|----------|
|azure-active-directory-b2c-spring-boot-starter| [spring-cloud-azure-starter-active-directory-b2c]|No                  |
|azure-active-directory-spring-boot-starter    | [spring-cloud-azure-starter-active-directory]    |No                  |
|azure-spring-boot-starter-cosmos              | [spring-cloud-azure-starter-data-cosmos]         |No                  |
|azure-keyvault-secrets-spring-boot-starter    | [spring-cloud-azure-starter-keyvault-secrets]    |No                  | 
|azure-servicebus-jms-spring-boot-starter      | [spring-cloud-azure-starter-servicebus-jms]      |No                  |  
|azure-spring-boot-starter-storage             | [spring-cloud-azure-starter-storage-blob]        |No                  |
|azure-spring-boot-metrics-starter             | N/A                                              |Yes                 |
|spring-data-gremlin-boot-starter              | N/A                                              |Yes                 |Vote [this issue](https://github.com/Azure/azure-sdk-for-java/issues/24773) if you want it be supported.

[spring-cloud-azure-starter-active-directory-b2c]: https://github.com/Azure/azure-sdk-for-java/tree/main/sdk/spring/spring-cloud-azure-starter-active-directory-b2c
[spring-cloud-azure-starter-active-directory]: https://github.com/Azure/azure-sdk-for-java/tree/main/sdk/spring/spring-cloud-azure-starter-active-directory
[spring-cloud-azure-starter-data-cosmos]: https://github.com/Azure/azure-sdk-for-java/tree/main/sdk/spring/spring-cloud-azure-starter-data-cosmos
[spring-cloud-azure-starter-keyvault-secrets]: https://github.com/Azure/azure-sdk-for-java/tree/main/sdk/spring/spring-cloud-azure-starter-keyvault-secrets
[spring-cloud-azure-starter-servicebus-jms]: https://github.com/Azure/azure-sdk-for-java/tree/main/sdk/spring/spring-cloud-azure-starter-servicebus-jms
[spring-cloud-azure-starter-storage-blob]: https://github.com/Azure/azure-sdk-for-java/tree/main/sdk/spring/spring-cloud-azure-starter-storage-blob
