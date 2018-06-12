## Overview
This sample illustrates how to use `azure-active-directory-spring-boot-starter` package to plugin JWT token filter into Spring Security filter chain. The filter injects `UserPrincipal` object that is associated with the thread of the current user request. User's AAD membership info, along with token claimsset, JWS object etc. are accessible from the object which can be used for role based authorization. Methods like `isMemberOf` is also supported.

### Get started
The sample is composed of two layers: Angular JS client and Spring Boot RESTful Web Service. You need to make some changes to get it working with your Azure AD tenant on both sides.

#### Application.properties
You need to have an registered app in your Azure AD tenant and create a security key.
If your azure account follows format xxx@xxx.partner.onmschina.cn, configure property `azure.activedirectory.environment=cn` to use [Azure China](https://docs.microsoft.com/en-us/azure/china/china-welcome), the default value is `global`.
Put Application ID and Key in `client-id` and `client-secret` respectively e.g.
`azure.activedirectory.client-id=xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx`
`azure.activedirectory.client-secret=ABCDEFGHIJKLMNOOPQRSTUVWXYZABCDEFGHIJKLMNOPQ`
List all the AAD groups `ActiveDirectoryGroups` that you want to have a Spring Security role object mapping to it. The role objects can then be used to manage access to resources that is behind Spring Security. e.g.
`azure.activedirectory.active-directory-groups=group1,group2`
You can use `@PreAuthorize` annotation or `UserPrincipal` to manage access to web API based on user's group membership. You will need to change `ROLE_group1` to groups you want to allow to access the API or you will get "Access is denied".

##### Note: 
- The sample retrieves user's group membership using Azure AD graph API which requires the registered app to have `Access the directory as the signed-in user` under `Delegated Permissions`. You need AAD admin privilege to be able to grant the permission in API ACCESS -> Required permission.
- Add `http://localhost:8080` as one of the `Reply URLs` in the settings of your registered app.

#### Angular JS
In `app.js`, make following changes. The client leverages Azure AD library for JS to handle AAD authentication in single page application. The following snippet of code configures adal provider for your registered app.
```
        adalProvider.init(
            {
                instance: 'https://login.microsoftonline.com/',
                tenant: 'your-aad-tenant',
                clientId: 'your-application-id',
                extraQueryParameter: 'nux=1',
                cacheLocation: 'localStorage',
            },
            $httpProvider
        );

```

### Give it a run

   - Use Maven 

     ```
     mvn clean install
     cd azure-active-directory-spring-boot-sample
     mvn spring-boot:run
     ```

   - Use Gradle 
   
     ```
     gradle clean bootRepackage
     java -jar build/libs/azure-active-directory-spring-boot-sample-0.0.1-SNAPSHOT.jar
     ```

* If running locally, browse to `http://localhost:8080` and click `Login` or `Todo List`, your browser will be redirected to `https://login.microsoftonline.com/` for authentication.
* Upon successful login, `Todo List` will give you a default item and you can perform add, update or delete operation. The backend RESTful API will accept or deny your request based on authenticated user roles.