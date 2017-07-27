## Usage

### Register your application

Go to [Application Registration Portal](https://apps.dev.microsoft.com/#/appList) and register your app. 

- Click `Add an app` button to start registration.
- Mark down your application id.
- Click `Add platform` button and choose the `Web` platform.
- Click `Generate New Password` to get your application secret. 
- Click `Add platform` button and choose the `Web` platform. Set the redirection URL to `http://localhost:8080/connect/microsoft`.

### Specify Credential

- Go to `application.properties` file and specify the app id and app secret respectively.
- Run the application. 