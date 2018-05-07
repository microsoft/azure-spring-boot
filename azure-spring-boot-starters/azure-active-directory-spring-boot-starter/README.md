## Overview
With Spring Starter for Azure Active Directory, now you can get started quickly to build the authentication workflow for a web application that uses Azure AD and OAuth 2.0 to secure its back end. It also enables developers to create a role based authorization workflow for a Web API secured by Azure AD, with the power of the Spring Security Filter Chain. 

### Implementation Summary
This package provides a Spring Security filter to validate the Jwt token from Azure AD. The Jwt token is also used to acquire a On-Behalf-Of token for Azure AD Graph API so that authenticated user's membership information is available for authorization of access of API resources. Below is a diagram that shows the layers and typical flow for Single Page Application with Spring Boot web API backend that uses the filter for Authentication and Authorization.
![Single Page Application + Spring Boot Web API + Azure AD](resource/spring-aad.png)

The authorization flow is composed of 3 phrases:
* Login with credentials and validate id_token from Azure AD 
* Get On-Behalf-Of token and membership info from Azure AD Graph API
* Evaluate the permission based on membership info to grant or deny access

### Register the Application in Azure AD
* **Register a new application**: Go to Azure Portal - Azure Active Directory - App registrations - New application registration to register the application in Azure Active Directory.  `Application ID` is `clientId` in `application.properties`.
* **Grant permissions to the application**: After application registration succeeded, go to API ACCESS - Required permissions - DELEGATED PERMISSIONS, tick `Access the directory as the signed-in user` and `Sign in and read user profile`. Click `Grant Permissions` (Note: you will need administrator privilege to grant permission).
* **Create a client secret key for the application**: Go to API ACCESS - Keys to create a secret key (`clientSecret`).

### Add Maven Dependency

`azure-active-directory-spring-boot-starter` is published on Maven Central Repository.
If you are using Maven, add the following dependency.

```xml
<dependency>
    <groupId>com.microsoft.azure</groupId>
    <artifactId>azure-active-directory-spring-boot-starter</artifactId>
    <version>0.2.3</version>
</dependency>
```

### Add Application Properties

Open `application.properties` file and add following properties.

```
azure.activedirectory.clientId=Application-ID-in-AAD-App-registrations
azure.activedirectory.clientSecret=Key-in-AAD-API-ACCESS
azure.activedirectory.ActiveDirectoryGroups=Aad-groups e.g. group1,group2,group3
```

If you're using [Azure China](https://docs.microsoft.com/en-us/azure/china/china-welcome), please append an extra line to the `application.properties` file:
```
azure.activedirectory.environment=cn
```

### Feature List  

Please refer to [azure-active-directory-spring-boot-sample](../../azure-spring-boot-samples/azure-active-directory-spring-boot-sample/README.md) for how to integrate Spring Security and Azure AD for authentication and authorization in a Single Page Application (SPA) scenario.

* Auto-wire `AADAuthenticationFilter` in `WebSecurityConfig.java` file 

```
@Autowired
private AADAuthenticationFilter aadAuthFilter;
```

* Role-based Authorization with annotation `@PreAuthorize("hasRole('GROUP_NAME')")`
* Role-based Authorization with method `isMemberOf()`

### Allow telemetry
Microsoft would like to collect data about how users use this Spring boot starter. Microsoft uses this information to improve our tooling experience. Participation is voluntary. If you don't want to participate, just simply disable it by setting below configuration in `application.properties`.
```
azure.mediaservices.allow-telemetry=false
```
Find more information about Azure Service Privacy Statement, please check [Microsoft Online Services Privacy Statement](https://www.microsoft.com/en-us/privacystatement/OnlineServices/Default.aspx). 

