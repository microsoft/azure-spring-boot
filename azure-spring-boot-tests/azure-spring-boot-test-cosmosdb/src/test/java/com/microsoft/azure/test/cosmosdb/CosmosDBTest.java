/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.test.cosmosdb;

import com.microsoft.azure.management.cosmosdb.CosmosDBAccount;
import com.microsoft.azure.management.cosmosdb.DatabaseAccountListKeysResult;
import com.microsoft.azure.mgmt.ClientSecretAccess;
import com.microsoft.azure.mgmt.Constants;
import com.microsoft.azure.mgmt.CosmosdbTool;
import com.microsoft.azure.mgmt.ResourceGroupTool;
import com.microsoft.azure.spring.autoconfigure.b2c.AADB2CAutoConfiguration;
import com.microsoft.azure.test.AppRunner;
import lombok.extern.slf4j.Slf4j;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
public class CosmosDBTest {

    private static CosmosdbTool cosmosdbTool;
    private static ClientSecretAccess access;
    private static CosmosDBAccount cosmosDBAccount;

    @BeforeClass
    public static void createCosmosDB() {
        access = ClientSecretAccess.load();
        cosmosdbTool = new CosmosdbTool(access);
        cosmosDBAccount = cosmosdbTool.createCosmosDBInNewGroup(Constants.TEST_RESOURCE_GROUP_NAME, "test-cosmosdb");
        cosmosdbTool.createDBAndAddCollection(cosmosDBAccount);
        log.info("------------------resources provision over------------------");
    }

    @AfterClass
    public static void deleteResourceGroup() {
        final ResourceGroupTool tool = new ResourceGroupTool(access);
        tool.deleteGroup(Constants.TEST_RESOURCE_GROUP_NAME);
        log.info("---------------------resources clean over------------------");
    }

    @Test(expected = NoSuchBeanDefinitionException.class)
    public void testCosmosStarterIsolating() {
        try (AppRunner app = new AppRunner(DummyApp.class)) {
            //set properties
            DatabaseAccountListKeysResult databaseAccountListKeysResult = cosmosDBAccount.listKeys();
            String masterKey = databaseAccountListKeysResult.primaryMasterKey();
            String endPoint = cosmosDBAccount.documentEndpoint();
            app.property("azure.cosmosdb.uri", endPoint);
            app.property("azure.cosmosdb.key", masterKey);
            app.property("azure.cosmosdb.database", cosmosdbTool.DATABASE_ID);

            //start app
            app.start();
            AADB2CAutoConfiguration bean = app.getBean(AADB2CAutoConfiguration.class);
            Assert.assertNull(bean);
            log.info("--------------------->test over");
        }
    }

    @Test
    public void testCosmosOperation() {
        try (AppRunner app = new AppRunner(DummyApp.class)) {
            //set properties
            DatabaseAccountListKeysResult databaseAccountListKeysResult = cosmosDBAccount.listKeys();
            String masterKey = databaseAccountListKeysResult.primaryMasterKey();
            String endPoint = cosmosDBAccount.documentEndpoint();
            app.property("azure.cosmosdb.uri", endPoint);
            app.property("azure.cosmosdb.key", masterKey);
            app.property("azure.cosmosdb.database", cosmosdbTool.DATABASE_ID);

            //start app
            app.start();
            UserRepository repository = app.getBean(UserRepository.class);
            final User testUser = new User("testId", "testFirstName", "testLastName", "test address line one");

            // Save the User class to Azure CosmosDB database.
            final Mono<User> saveUserMono = repository.save(testUser);
            final Flux<User> firstNameUserFlux = repository.findByFirstName("testFirstName");

            //  Nothing happens until we subscribe to these Monos.
            //  findById will not return the user as user is not present.
            final Mono<User> findByIdMono = repository.findById(testUser.getId());
            final User findByIdUser = findByIdMono.block();
            org.springframework.util.Assert.isNull(findByIdUser, "User must be null");

            final User savedUser = saveUserMono.block();
            org.springframework.util.Assert.state(savedUser != null, "Saved user must not be null");
            org.springframework.util.Assert.state(savedUser.getFirstName().equals(testUser.getFirstName()), "Saved user first name doesn't match");

            firstNameUserFlux.collectList().block();
            final Optional<User> optionalUserResult = repository.findById(testUser.getId()).blockOptional();
            org.springframework.util.Assert.isTrue(optionalUserResult.isPresent(), "Cannot find user.");

            final User result = optionalUserResult.get();
            org.springframework.util.Assert.state(result.getFirstName().equals(testUser.getFirstName()), "query result firstName doesn't match!");
            org.springframework.util.Assert.state(result.getLastName().equals(testUser.getLastName()), "query result lastName doesn't match!");
            log.info("findOne in User collection get result: {}", result.toString());
            log.info("--------------------->test over");
        }
    }
}
