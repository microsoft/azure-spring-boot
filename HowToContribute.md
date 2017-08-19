# How to Build and Contribute
This instruction is guideline for building and code contribution.

## Prequisites
- JDK 1.8 and above
- [Maven](http://maven.apache.org/) 3.0 and above

## Build from source
To build the project, run maven commands.

```bash
git clone https://github.com/Microsoft/azure-spring-boot-starters.git 
cd azure-spring-boot-starters
mvnw clean install
```

## Test
There're 2 profiles: `dev` and `integration-test-azure`. Default profile is `dev`. Profile `integration-test-azure` will trigger integration test execution.

- Run unit tests
```bash
mvnw clean install
```

- Run unit tests and integration tests
 
  >**NOTE** Please note that integration test will automatically create a Azure Cosmos DB Document API in your Azure subscription, then there will be **Azure usage fee.**
 
  Integration tests will require a Azure Subscription. If you don't already have an Azure subscription, you can activate your [MSDN subscriber benefits](https://azure.microsoft.com/en-us/pricing/member-offers/msdn-benefits-details/) or sign up for a [free Azure account](https://azure.microsoft.com/en-us/free/). 
  
  1. Create a service principal by using Azure Cli or by [Azure Portal](https://docs.microsoft.com/en-us/azure/azure-resource-manager/resource-group-create-service-principal-portal). 
  2. After service principal ready, set environment variables CLIENT_ID, CLIENT_KEY and TENANT_ID, where value of them are service principal id, key and tenant id.
  3. Run maven command with `integration-test-azure` profile. 
  
```bash
set CLIENT_ID=your-azure-service-principal-id
set CLIENT_KEY=your-azure-service-principal-key
set TENANT_ID=your-azure-subscription-tenant-id
mvnw -P integration-test-azure clean install
```

- Skip tests execution
```bash
mvnw clean install -DskipTests
```

## Version management
Developing version naming convention is like `0.1.5-beta`. Release version naming convention is like `0.1.5`. Please don't update version if no release plan. 

## CI
Both [travis](https://travis-ci.org/Microsoft/azure-spring-boot-starters) and [appveyor](https://ci.appveyor.com/project/yungez/azure-spring-boot-starters) CI is enabled.

## Contribute to code
Code contribution is welcome. To contribute to existing code or add new Starter, please make sure below check list are checked.
- [ ] Build pass. checkstyle and findbugs is enabled by default. Please check [checkstyle.xml](./common/config/checkstyle.xml) to learn detail checkstyle configuration.
- [ ] Documents are updated to aligning with code.
- [ ] New starter must have sample folder contains sample code and corresponding readme file.
- [ ] Code coverage for new codes >= 65%. Code coverage check is enabled with 65% bar.

