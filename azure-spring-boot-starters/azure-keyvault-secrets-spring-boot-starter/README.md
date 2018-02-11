## Azure Key Vault Secrets Spring boot starter
Azure Key Vault Secrets Spring boot starter is Spring starter for [Azure Key Vault Secrets](https://docs.microsoft.com/en-us/rest/api/keyvault/about-keys--secrets-and-certificates#BKMK_WorkingWithSecrets). With this starter, Azure Key Vault is added as one of Spring PropertySource, so secrets stored in Azure Key Vault could be easily used and conveniently accessed like other externalized configuration property, e.g. properties in files.

## Sample Code
Please refer to [sample project here](../../azure-spring-boot-samples/azure-keyvault-secrets-spring-boot-sample).

## Quick Start

### Add the dependency

"azure-keyvault-secrets-spring-boot-starter" is published on Maven Central Repository.  
If you are using Maven, add the following dependency.  

```xml
<dependency>
    <groupId>com.microsoft.azure</groupId>
    <artifactId>azure-keyvault-secrets-spring-boot-starter</artifactId>
    <version>0.2.3</version>
</dependency>
```

### Add the property setting

Open `application.properties` file and add below properties to specify your Azure Key Vault url, Azure service principal client id and client key. `azure.keyvault.enabled` is used to turn on/off Azure Key Vault Secret property source, default is true. `azure.keyvault.token-acquiring-timeout-seconds` is used to specify the timeout in seconds when acquiring token from Azure AAD. Default value is 60 seconds. This property is optional.
```
azure.keyvault.enabled=true
azure.keyvault.uri=put-your-azure-keyvault-uri-here
azure.keyvault.client-id=put-your-azure-client-id-here
azure.keyvault.client-key=put-your-azure-client-key-here
azure.keyvault.token-acquire-timeout-seconds=60
```


### Save secrets in Azure Key Vault
Save secrets in Azure Key Vault through [Azure Portal](https://blogs.technet.microsoft.com/kv/2016/09/12/manage-your-key-vaults-from-new-azure-portal/) or [Azure CLI](https://docs.microsoft.com/en-us/cli/azure/keyvault/secret).

You can use the following Azure CLI command to save secrets, if Key Vault is already created.
```
az keyvault secret set --name <your-property-name> --value <your-secret-property-value> --vault-name <your-keyvault-name>
```
> NOTE
> To get detail steps on how setup Azure Key Vault, please refer to sample code readme section ["Setup Azure Key Vault"](../../azure-spring-boot-samples/azure-keyvault-secrets-spring-boot-sample/README.md)

> **IMPORTANT** 
> Allowed secret name pattern in Azure Key Vault is ^[0-9a-zA-Z-]+$, for some Spring system properties contains `.` like spring.datasource.url, do below workaround when you save it into Azure Key Vault: simply replace `.` to `-`. `spring.datasource.url` will be saved with name `spring-datasource-url` in Azure Key Vault. While in client application, use original `spring.datasource.url` to retrieve property value, this starter will take care of transformation for you. Purpose of using this way is to integrate with Spring existing property setting.

### Get Key Vault secret value as property
Now, you can get Azure Key Vault secret value as a configuration property.

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

