/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.test.cosmosdb;

import com.microsoft.azure.management.cosmosdb.CosmosDBAccount;
import com.microsoft.azure.management.cosmosdb.DatabaseAccountListKeysResult;
import com.microsoft.azure.management.resources.fluentcore.utils.SdkContext;
import com.microsoft.azure.mgmt.ClientSecretAccess;
import com.microsoft.azure.mgmt.ConstantsHelper;
import com.microsoft.azure.mgmt.CosmosdbTool;
import com.microsoft.azure.mgmt.ResourceGroupTool;
import com.microsoft.azure.test.AppRunner;
import lombok.extern.slf4j.Slf4j;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Optional;


@Slf4j
public class CosmosDBIT {

    private static CosmosdbTool cosmosdbTool;
    private static ClientSecretAccess access;
    private static CosmosDBAccount cosmosDBAccount;
    private static String resourceGroupName;

    @BeforeClass
    public static void createCosmosDB() {
        access = ClientSecretAccess.load();
        cosmosdbTool = new CosmosdbTool(access);
        resourceGroupName = SdkContext.randomResourceName(ConstantsHelper.TEST_RESOURCE_GROUP_NAME_PREFIX, 30);
        cosmosDBAccount = cosmosdbTool.createCosmosDBInNewGroup(resourceGroupName, "test-cosmosdb");
        cosmosdbTool.createDBAndAddCollection(cosmosDBAccount);
        log.info("------------------resources provision over------------------");
    }

    @AfterClass
    public static void deleteResourceGroup() {
        final ResourceGroupTool tool = new ResourceGroupTool(access);
        tool.deleteGroup(resourceGroupName);
        log.info("---------------------resources clean over------------------");
    }

    @Test
    public void testCosmosOperation() {
        try (AppRunner app = new AppRunner(DummyApp.class)) {
            //set properties
            final DatabaseAccountListKeysResult databaseAccountListKeysResult = cosmosDBAccount.listKeys();
            final String masterKey = databaseAccountListKeysResult.primaryMasterKey();
            final String endPoint = cosmosDBAccount.documentEndpoint();
            app.property("azure.cosmosdb.uri", endPoint);
            app.property("azure.cosmosdb.key", masterKey);
            app.property("azure.cosmosdb.database", cosmosdbTool.DATABASE_ID);
            app.property("azure.cosmosdb.populateQueryMetrics", String.valueOf(true));

            //start app
            app.start();
            final UserRepository repository = app.getBean(UserRepository.class);
            final User testUser = new User("testId",
                    "testFirstName",
                    "testLastName",
                    "test address line one");
            repository.deleteAll();
            repository.save(testUser);

            final Optional<User> opResult = repository.findById(testUser.getId());
            Assert.assertTrue("Cannot find user.", opResult.isPresent());

            final User result = opResult.get();
            Assert.assertEquals("query result firstName doesn't match!",
                    result.getFirstName(),
                    testUser.getFirstName());
            Assert.assertEquals("query result lastName doesn't match!",
                    result.getLastName(),
                    testUser.getLastName());
            log.info("findOne in User collection get result: {}", result.toString());

            app.close();
            log.info("--------------------->test over");
        }
    }
}
