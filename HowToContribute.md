# How to Build and Contribute
This instruction is guideline for building and code contribution.

## Prequisites
- JDK 1.8 and above
- [Maven](http://maven.apache.org/) 3.0 and above

## Build from source
To build the project, run maven commands.

```bash
git clone https://github.com/Microsoft/azure-spring-boot.git 
cd azure-spring-boot
mvnw clean install
```

## Test

- Run unit tests
```bash
mvnw clean install
```

- Skip tests execution
```bash
mvnw clean install -DskipTests
```

## Version management
Developing version naming convention is like `0.1.8-SNAPSHOT`. Release version naming convention is like `0.1.8`. Please don't update version if no release plan. 

## CI
Both [travis](https://travis-ci.org/Microsoft/azure-spring-boot) and [appveyor](https://ci.appveyor.com/project/yungez/azure-spring-boot) CI is enabled.

## Contribute to code
Code contribution is welcome. To contribute to existing code or add new Starter, please make sure below check list are checked.
- [ ] Build pass. checkstyle and findbugs is enabled by default. Please check [checkstyle.xml](config/checkstyle.xml) to learn detail checkstyle configuration.
- [ ] Documents are updated to aligning with code.
- [ ] New starter must have sample folder contains sample code and corresponding readme file.
- [ ] Code coverage for new codes >= 65%. Code coverage check is enabled with 65% bar.

