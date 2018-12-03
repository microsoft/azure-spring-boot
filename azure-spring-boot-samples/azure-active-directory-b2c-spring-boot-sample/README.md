### How to configure

#### Create your Azure Active Directory B2C Tenant

Follow the guide of [AAD B2C tenant creation](https://docs.microsoft.com/en-us/azure/active-directory-b2c/active-directory-b2c-tutorials-web-app).

#### Register your Azure Active Directory B2C and create polices

Follow the guide of [AAD B2C application registry and policies creation](https://docs.microsoft.com/en-us/azure/active-directory-b2c/active-directory-b2c-tutorials-web-app)

#### Configure application.yml

To make the sample work, some changes about url is required. Please replace the placeholder as follow:

| Placeholder                             | URL                                   |
| :-------------------------------------- | :----------------------               |
| `${your-policy-reply-url}`              | `http://localhost:8080/`              |
| `${your-logout-success-url}`            | `http://localhost:8080/`              |
| `${your-url-to-process-password-reset}` | `http://localhost:8080/password-reset`|
| `${your-url-to-process-profile-edit}`   | `http://localhost:8080/profile-edit`  |

```yaml
server:
  port: 8080
azure:
  activedirectory:
    b2c:
      tenant: ${your-tenant-name}
      client-id: ${your-application-id}
      policies:
        sign-up-or-sign-in: # Required
          name: ${your-policy-name}
          reply-url: ${your-policy-reply-url}
        password-reset:     # Optional
          name: ${your-policy-name}
          reply-url: ${your-policy-reply-url}
        profile-edit:       # Optional
          name: ${your-policy-name}
          reply-url: ${your-policy-reply-url}

      # Only absolute URL is supported in following configuration
      logout-success-url: ${your-logout-success-url}            # Required
      password-reset-url: ${your-url-to-process-password-reset} # Optional
      profile-edit-url: ${your-url-to-process-profile-edit}     # Optional
```

### How to run

   - Use Maven 

     ```
     # Under azure-spring-boot project root directory
     mvn clean install -DskipTests
     cd azure-spring-boot-samples
     cd azure-active-directory-b2c-spring-boot-sample
     mvn spring-boot:run
     ```

### Check the authentication with policies.
	
1. Access `http://localhost:8080/` as home page.
2. Sign up/in.
3. Access greeting button.
4. Logout.
5. Sign in.
6. Profile edit.
7. Password reset.
8. Logout
9. Sign in.

### FAQ

#### Sign in with loops to B2C endpoint ?
This issue almost due to polluted cookies of `localhost`. Clean up cookies of `localhost` and try it again.

#### More identity providers from AAD B2C login ?
Follow the guide of [Set up Google account with AAD B2C](https://docs.microsoft.com/en-us/azure/active-directory-b2c/active-directory-b2c-setup-goog-app).
And also available for Amazon, Azure AD, FaceBook, Github, Linkedin and Twitter.
