## How to configure

### Create your Azure Active Directory B2C Tenant

Follow the guide of [AAD B2C tenant creation](https://docs.microsoft.com/en-us/azure/active-directory-b2c/active-directory-b2c-tutorials-web-app).

### Register your Azure Active Directory B2C and create polices

Follow the guide of [AAD B2C application registry and policies creation](https://docs.microsoft.com/en-us/azure/active-directory-b2c/active-directory-b2c-tutorials-web-app).

### Configure the sample

##### Azure AD B2C portal

1. Add `http://localhost:8080/home` as your Azure AD B2C application `reply URL`.

##### Application.yml

1. Fill in `${your-tenant-name}`, `${your-client-id}` and `${your-client-secret}` from Azure AD B2C portal `Applications`.
2. Fill in the `${your-sign-up-or-in-policy-value}`, `${your-profile-edit-policy-value}` and
`${your-password-reset-policy-value}` from the portal `User flows`.

```yaml
azure:
  activedirectory:
    b2c:
      tenant: ${your-tenant-name}
      client-id: ${your-client-id}
      client-secret: ${your-client-secret}
      reply-url: http://localhost:8080/home # should be absolute url.
      logout-success-url: http://localhost:8080/login
      policies:
        sign-up-or-sign-in: ${your-sign-up-or-in-policy-value}
        profile-edit: ${your-profile-edit-policy-value}
        password-reset: ${your-password-reset-policy-value}
```

##### Templates greeting.html and home.html
1. Fill in the `${your-profile-edit-policy-value}` and `${your-password-reset-policy-value}` from the portal `User flows`.

### How to run

   - Use Maven 

     ```
     # Under azure-spring-boot project root directory
     mvn clean install -DskipTests
     cd azure-spring-boot-samples
     cd azure-active-directory-b2c-oidc-spring-boot-sample
     mvn spring-boot:run
     ```

### Check the authentication with policies.
	
1. Access `http://localhost:8080/` as index page.
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
