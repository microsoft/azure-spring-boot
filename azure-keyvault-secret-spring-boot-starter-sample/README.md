#Azure KeyVault secret Spring Boot Starter Sample
This sample illustrate how to use [Azure KeyVault secret Spring Boot Starter](../azure-keyvault-secret-spring-boot-starter/README.md).

In this sample, a secret with name "spring-datasource-url" is set into Azure KeyVault, sample Spring application will read this as property out from Azure KeyVault.

## Setup Azure KeyVault
First, we need to set secret "spring-datasource-url" into Azure KeyVault.

- Create one azure service principle by Azure CLI or through [Azure Portal](https://docs.microsoft.com/en-us/azure/azure-resource-manager/resource-group-create-service-principal-portal). Save your service principle id and password, for later use.
az cli commands as below.
```bash
az login
az account set --subscription <your_subscription_id>

# create azure service principle by azure cli
az ad app create --display-name <your_app_name> --identifier-uris http://test.com/test --homepage http://test.com/test
# save the appId from output
az ad sp create --id <app_id_created_from_above_step>
```
Save the service principle id and password in output.

- Create Azure KeyVault by Azure CLI or through [Azure Portal](http://www.rahulpnath.com/blog/managing-key-vault-through-azure-portal/). Give permission to service principle created at step 1. az cli command as below.
```bash
az keyvault create --name <your_keyvault_name> --resource-group <your_resource_group> --location <location> --enabled-for-deployment true --enabled-for-disk-encryption true --enabled-for-template-deployment true --sku standard
az keyvault set-policy --name <your_keyvault_name> --secret-permission all --object-id <your_sp_id_create_in_step1>
```
Save the KeyVault uri in output for later use.

- Set secret in Azure KeyVault by Azure CLI or through Azure Portal. az cli commands as below
```bash
az keyvault secret set --name spring-datasource-url --value jdbc:mysql://localhost:3306/moviedb --vault-name <your_keyvault_name>
az keyvault secret set --name <yourSecretPropertyName> --value <yourSecretPropertyVaule> --vault-name <your_keyvault_name>
```

## Add Dependency

"azure-keyvault-secret-spring-boot-starter" is published on Maven Central Repository.  
If you are using Maven, add the following dependency.  

```xml
<dependency>
    <groupId>com.microsoft.azure</groupId>
    <artifactId>azure-keyvault-secret-spring-boot-starter</artifactId>
    <version>0.1.3</version>
</dependency>
```

## Add the property setting
Open `application.properties` file and add below properties to specify your Azure KeyVault url, Azure service principle client id and client key.

```
#azure.keyvault.vaulturi=put-your-azure-keyvault-uri-here
#azure.keyvault.clientid=put-your-azure-serviceprinciple-id-here
#azure.keyvault.clientkey=put-your-azure-serviceprinciple-key-here
#azure.keyvault.enabled=true
```

## Get KeyVault secret value as property
Now, you can get Azure KeyVault secret value as property.

```
@SpringBootApplication
public class SampleApplication implements CommandLineRunner {

    @Value("${yourSecretPropertyName}")
    private String mySecretProperty;
    
    @Value("${spring.datasource.url}")
    private String dbUrl;

    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }

    public void run(String... varl) throws Exception {        
        System.out.println("property yourSecretPropertyName value is: " + mySecretProperty);
        System.out.println("property spring.datasource.url is: " + dbUrl);
    }

}
```