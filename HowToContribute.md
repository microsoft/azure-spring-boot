# How to Build and Contribute
This instruction is guideline for building and code contribution.

## Prequisites
- JDK 1.8 and above
- [Maven](http://maven.apache.org/) 3.0 and above

## Build from source
To build the project, run maven commands. Building from submodule folder is broken due to one known [issue](https://github.com/Microsoft/azure-spring-boot-starters/issues/46), will add instruction later once the issue is fixed.

```bash
git clone https://github.com/Microsoft/azure-spring-boot-starters.git 
cd azure-spring-boot-starters
mvn clean install
```

## Test
There're 2 profiles: `dev` and `integration-test`. Default profile is `dev`. Profile `integration-test` will trigger integration test execution.

- Run unit tests
```bash
mvn clean install
```

- Run unit tests and integration tests
 
  >**NOTE** Please note that integration-test will automatically create a Azure Cosmos DB Document API in your Azure subscription, then there will be **Azure usage fee.**
 
  Integration tests will require a Azure Subscription. If you don't already have an Azure subscription, you can activate your [MSDN subscriber benefits](https://azure.microsoft.com/en-us/pricing/member-offers/msdn-benefits-details/) or sign up for a [free Azure account](https://azure.microsoft.com/en-us/free/). 
  
  1. Create a service principal by using Azure Cli or by [Azure Portal](https://docs.microsoft.com/en-us/azure/azure-resource-manager/resource-group-create-service-principal-portal). 
  2. After service principal ready, set environment variables CLIENT_ID, CLIENT_KEY and TENANT_ID, where value of them are service principal id, key and tenant id.
  3. Run maven command with `integration-test` profile. 
  
```bash
set CLIENT_ID=your-azure-service-principal-id
set CLIENT_KEY=your-azure-service-principal-key
set TENANT_ID=your-azure-subscription-tenant-id
mvn -P integration-test clean install
```

- Skip tests execution
```bash
mvn clean install -DskipTests
```

## Contribute to code
Code contribution is welcome. To contribute to existing code or add new Starter, please make sure below check list are checked.
- [ ] Build pass. checkstyle and findbugs is enabled by default.
- [ ] Documents are updated to aligning with code.
- [ ] New starter must have sample folder contains sample code and corresponding readme file.
- [ ] Code coverage for new codes >= 65%

