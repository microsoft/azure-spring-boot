/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.test.keyvault;

import com.microsoft.azure.management.keyvault.Vault;
import static com.microsoft.azure.management.resources.fluentcore.utils.SdkContext.randomResourceName;
import com.microsoft.azure.mgmt.ClientSecretAccess;
import static com.microsoft.azure.mgmt.ConstantsHelper.TEST_RESOURCE_GROUP_NAME_PREFIX;
import com.microsoft.azure.mgmt.KeyVaultTool;
import com.microsoft.azure.mgmt.ResourceGroupTool;
import com.microsoft.azure.test.AppRunner;
import lombok.extern.slf4j.Slf4j;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * This test requires the following environment variables to be set.
 *
 * <ul>
 * <li>AZURE_TENANT - your Azure tenant id</li>
 * <li>AZURE_SUBSCRIPTION - your Azure subscription id</li>
 * <li>AZURE_CLIENT_ID - an Application (client) id</li>
 * <li>AZURE_CLIENT_SECRET - an Application (client) secret</li>
 * </ul>
 */
@Slf4j
public class MultipleKeyVaultsIT {

    /**
     * Store client secret access.
     */
    private static ClientSecretAccess access;

    /**
     * Stores 'keyVault1' key vault.
     */
    private static Vault keyVault1;

    /**
     * Stores 'keyVault2' key vault.
     */
    private static Vault keyVault2;

    /**
     * Stores the resource group name.
     */
    private static String resourceGroupName;

    /**
     * Delete the created resource group.
     */
    @AfterClass
    public static void afterClass() {
        final ResourceGroupTool tool = new ResourceGroupTool(access);
        tool.deleteGroup(resourceGroupName);
    }

    /**
     * Create the following:
     *
     * <ol>
     * <li>A test resource group</li>
     * <li>A 'keyVault1' key vault in the test resource group</li>
     * <li>A 'keyVault2' key vault in the test resource group</li>
     * </ol>
     */
    @BeforeClass
    public static void beforeClass() {
        access = ClientSecretAccess.load();
        resourceGroupName = randomResourceName(TEST_RESOURCE_GROUP_NAME_PREFIX, 30);
        final KeyVaultTool tool = new KeyVaultTool(access);
        keyVault1 = tool.createVaultInNewGroup(resourceGroupName, "keyvault1-");
        keyVault1.secrets().define("key1").withValue("vault1").create();
        keyVault1.secrets().define("duplicateKey").withValue("vault1").create();
        keyVault2 = tool.createVaultInNewGroup(resourceGroupName, "keyvault2-");
        keyVault2.secrets().define("key2").withValue("vault2").create();
        keyVault2.secrets().define("duplicateKey").withValue("vault2").create();
    }

    /**
     * Test getting value from 'keyvault1'.
     */
    @Test
    public void testGetValueFromKeyVault1() {
        try (AppRunner app = new AppRunner(TestApp.class)) {
            app.property("azure.keyvault.order", "keyvault1");
            app.property("azure.keyvault.keyvault1.uri", keyVault1.vaultUri());
            app.property("azure.keyvault.keyvault1.enabled", "true");
            app.property("azure.keyvault.keyvault1.client-id", access.clientId());
            app.property("azure.keyvault.keyvault1.client-key", access.clientSecret());
            app.property("azure.keyvault.keyvault1.tenant-id", access.tenant());
            app.start("dummy");
            assertEquals("vault1", app.getProperty("key1"));
            app.close();
        }
    }

    /**
     * Test getting value from 'keyvault2'.
     */
    @Test
    public void testGetValueFromKeyVault2() {
        try (AppRunner app = new AppRunner(TestApp.class)) {
            app.property("azure.keyvault.order", "keyvault2");
            app.property("azure.keyvault.keyvault2.uri", keyVault2.vaultUri());
            app.property("azure.keyvault.keyvault2.enabled", "true");
            app.property("azure.keyvault.keyvault2.client-id", access.clientId());
            app.property("azure.keyvault.keyvault2.client-key", access.clientSecret());
            app.property("azure.keyvault.keyvault2.tenant-id", access.tenant());
            app.start("dummy");
            assertEquals("vault2", app.getProperty("key2"));
            app.close();
        }
    }

    /**
     * Test getting value for a duplicate key which should resolve to the value
     * in 'keyvault1' as that is the first one of the configured key vaults.
     */
    @Test
    public void testGetValueForDuplicateKey() {
        try (AppRunner app = new AppRunner(TestApp.class)) {
            app.property("azure.keyvault.order", "keyvault1, keyvault2");
            app.property("azure.keyvault.keyvault1.uri", keyVault1.vaultUri());
            app.property("azure.keyvault.keyvault1.enabled", "true");
            app.property("azure.keyvault.keyvault1.client-id", access.clientId());
            app.property("azure.keyvault.keyvault1.client-key", access.clientSecret());
            app.property("azure.keyvault.keyvault1.tenant-id", access.tenant());
            app.property("azure.keyvault.keyvault2.uri", keyVault2.vaultUri());
            app.property("azure.keyvault.keyvault2.enabled", "true");
            app.property("azure.keyvault.keyvault2.client-id", access.clientId());
            app.property("azure.keyvault.keyvault2.client-key", access.clientSecret());
            app.property("azure.keyvault.keyvault2.tenant-id", access.tenant());
            app.start("dummy");
            assertEquals("vault1", app.getProperty("duplicateKey"));
            app.close();
        }
    }

    /**
     * Test getting value from a vault configured both with single and the
     * multiple vault support.
     */
    @Test
    public void testGetValueFromSingleVault() {
        try (AppRunner app = new AppRunner(TestApp.class)) {
            app.property("azure.keyvault.enabled", "true");
            app.property("azure.keyvault.uri", keyVault1.vaultUri());
            app.property("azure.keyvault.client-id", access.clientId());
            app.property("azure.keyvault.client-key", access.clientSecret());
            app.property("azure.keyvault.tenant-id", access.tenant());
            app.property("azure.keyvault.order", "keyvault2");
            app.property("azure.keyvault.keyvault2.enabled", "true");
            app.property("azure.keyvault.keyvault2.uri", keyVault2.vaultUri());
            app.property("azure.keyvault.keyvault2.client-id", access.clientId());
            app.property("azure.keyvault.keyvault2.client-key", access.clientSecret());
            app.property("azure.keyvault.keyvault2.tenant-id", access.tenant());
            app.start("dummy");
            assertEquals("vault1", app.getProperty("key1"));
            assertEquals("vault2", app.getProperty("key2"));
            app.close();
        }
    }

    /**
     * Defines the Spring Boot test application.
     */
    @SpringBootApplication
    public static class TestApp {
    }
}
