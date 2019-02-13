## How to configure

### Create your Azure Active Directory B2C tenant

Follow the guide of [AAD B2C tenant creation](https://docs.microsoft.com/en-us/azure/active-directory-b2c/tutorial-create-tenant).

### Register your Azure Active Directory B2C application

Follow the guide of [AAD B2C application registry](https://docs.microsoft.com/en-us/azure/active-directory-b2c/tutorial-register-applications).
Please make sure that your b2c application `reply URL` contains `http://localhost:8080/home`.

### Create user flows

Follow the guide of [AAD B2C user flows creation](https://docs.microsoft.com/en-us/azure/active-directory-b2c/active-directory-b2c-tutorials-web-app).

### Configure the sample

#### Application.yml

1. Fill in `${your-tenant-name}`, `${your-client-id}` and `${your-client-secret}` from Azure AD B2C portal `Applications`.
2. Fill in the `${your-sign-up-or-in-policy-value}`, `${your-profile-edit-policy-value}` and
`${your-password-reset-policy-value}` from the portal `User flows`.
3. Replace `${your-reply-url}` to `http://localhost:8080/home`.
4. Replace `${your-logout-success-url}` to `http://localhost:8080/login`.

```yaml
azure:
  activedirectory:
    b2c:
      tenant: ${your-tenant-name}
      client-id: ${your-client-id}
      client-secret: ${your-client-secret}
      reply-url: ${your-reply-url} # should be absolute url.
      logout-success-url: ${your-logout-success-url}
      policies:
        sign-up-or-sign-in: ${your-sign-up-or-in-policy-value}
        profile-edit: ${your-profile-edit-policy-value}      # optional
        password-reset: ${your-password-reset-policy-value}  # optional
```

#### Templates greeting.html and home.html
1. Fill in the `${your-profile-edit-policy-value}` and `${your-password-reset-policy-value}` from the portal `User flows`.
Please make sure that these two placeholders should be the same as `application.yml` respectively.

### How to run

* Maven 

```
# Under azure-spring-boot project root directory
mvn clean install -DskipTests
cd azure-spring-boot-samples
cd azure-active-directory-b2c-oidc-spring-boot-sample
mvn spring-boot:run
```

### Validation
	
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
