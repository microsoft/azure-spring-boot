## Overview
Azure KeyVault secret Spring boot starter is Spring starter for [Azure KeyVault secret](https://docs.microsoft.com/en-us/rest/api/keyvault/about-keys--secrets-and-certificates#BKMK_WorkingWithSecrets). With this starter, Azure KeyVault is added as Spring PropertySource, so secrets saved in Azure KeyVault could be used easily and conviniently as normal property like other externalized property source, e.g. property files.

## Sample Code
Pls refer to [sample project here](../azure-keyvault-secret-spring-boot-starter-sample).

## Quick Start

### Add the dependency

"azure-keyvault-secret-spring-boot-starter" is published on Maven Central Repository.  
If you are using Maven, add the following dependency.  

```xml
<dependency>
    <groupId>com.microsoft.azure</groupId>
    <artifactId>azure-keyvault-secret-spring-boot-starter</artifactId>
    <version>0.1.3</version>
</dependency>
```

### Add the property setting

Open `application.properties` file and add below properties to specify your Azure KeyVault url, Azure service principle client id and client key.

```
#azure.keyvault.uri=put-your-azure-keyvault-uri-here
#azure.keyvault.client-id=put-your-azure-client-id-here
#azure.keyvault.client-key=put-your-azure-client-key-here
#azure.keyvault.enabled=true
```


### Set secrets in Azure KeyVault
Set secrets in Azure KeyVault through [Azure Portal](https://blogs.technet.microsoft.com/kv/2016/09/12/manage-your-key-vaults-from-new-azure-portal/) or [Azure CLI](https://docs.microsoft.com/en-us/cli/azure/keyvault/secret).

Below is sample Azure CLI command to set secret, if KeyVault is already created.
```
az keyvault secret set --name <your-property-name> --value <your-secret-property-value> --vault-name <your-keyvault-name>
```
> NOTE
> To get detail steps of setup Azure KeyVault, please refer to sample code readme section ["Setup Azure KeyVault"](../azure-keyvault-secret-spring-boot-starter-sample/README.md)

> **IMPORTANT** 
> Allowed secret name pattern in Azure KeyVault is ^[0-9a-zA-Z-]+$, so for some Spring system properties like spring.datasource.url, when you save it into Azure KeyVault, simply replace `.` to `-`, so save `spring-datasource-url` in Azure KeyVault to workaround it. While in client application, still use original `spring.datasource.url` to retrieve property value, this starter will take care of transformation for you. Purpose of using this way is to integrate with Spring existing property setting.

### Get KeyVault secret value as property
Now, you can get Azure KeyVault secret value as property.

```
@SpringBootApplication
public class SampleApplication implements CommandLineRunner {

    @Value("${your-property-name}")
    private String mySecretProperty;

    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }

    public void run(String... varl) throws Exception {        
        System.out.println( "property your-property-name value is: " + mySecretProperty);
    }

}
```



