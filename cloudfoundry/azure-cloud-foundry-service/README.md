## Azure Cloud Foundry Service
This project provides the ability to to automatically inject credentials from Cloud Foundry into your
applications consuming Azure services.  It does this by reading the VCAP_SERVICES environment 
variable and setting the appropriate properties on the azure autoconfiguration projects.

### Add the dependency

`azure-cloud-foundry-service` is published on Maven Central Repository.  
If you are using Maven, add the following dependency.  

```xml
<dependency>
    <groupId>com.microsoft.azure</groupId>
    <artifactId>azure-cloud-foundry-service</artifactId>
    <version>0.1.5</version>
</dependency>
```


