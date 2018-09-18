/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package sample.sqlserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

import java.util.Optional;
import java.util.UUID;

@SpringBootApplication
public class SqlServerSampleApplication implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlServerSampleApplication.class);

    @Autowired
    private PersonRepository repository;

    @Autowired
    ApplicationContext context;

    public static void main(String[] args) {
        SpringApplication.run(SqlServerSampleApplication.class, args);
    }

    public void run(String... var1) throws Exception {

        final Person testUser = new Person(UUID.randomUUID().toString(), "testFirstName",
                                    "testLastName", "test address line one");

        repository.deleteAll();
        repository.save(testUser);

        final Optional<Person> opResult = repository.findById(testUser.getId());
        Assert.isTrue(opResult.isPresent(), "Cannot find user.");

        final Person result = opResult.get();
        Assert.state(result.getFirstName().equals(testUser.getFirstName()), "query result firstName doesn't match!");
        Assert.state(result.getLastName().equals(testUser.getLastName()), "query result lastName doesn't match!");

        LOGGER.info("findById in User collection get result: {}", result.toString());
    }
}
