## Overview

This sample project demonstrates how to use Microsoft Graph via Spring Boot Starter `microsoft-graph-spring-social-starter`.
It is a simple web application that accesses basic profile data from user's Microsoft account. 

## Prerequisites

* A [Java Development Kit (JDK)](http://www.oracle.com/technetwork/java/javase/downloads/), version 1.8.

* [Apache Maven](http://maven.apache.org/), version 3.0 or later.

## Usage

### Register your application

Go to [Application Registration Portal](https://apps.dev.microsoft.com/#/appList) and register your app. 

- Click `Add an app` button to start registration.
- Mark down your application id.
- Click `Add platform` button and choose the `Web` platform.
- Click `Generate New Password` to get your application secret. 
- Click `Add platform` button and choose the `Web` platform. Set the redirection URL to `http://localhost:8080/connect/microsoft`.

### Config the sample

1. Navigate to `src/main/resources` and open `application.properties`.
2. Fill in the `app-id` and `app-secret`. 

### Run the sample

1. Change directory to folder `microsoft-graph-spring-social-starter-sample`.
2. Run below commands. 

```
mvn package
java -jar target/microsoft-graph-spring-social-starter-sample-0.0.1-SNAPSHOT.jar
```

### Make your own REST API call

This starter implements a small subset of Objects/APIs available via Microsoft Graph (GET /me, POST /me/sendMail, GET /me/messages). In addition, it demonstrates how to make custom REST API calls (see function `getContacts` in `HelloController.java` as an example). Please check the detailed [document](https://developer.microsoft.com/en-us/graph/docs/concepts/overview) for more details about Microsoft Graph. 
After reading the document, you should be aware of the exact REST API and what objects you should prepare or expect from the REST API call.
