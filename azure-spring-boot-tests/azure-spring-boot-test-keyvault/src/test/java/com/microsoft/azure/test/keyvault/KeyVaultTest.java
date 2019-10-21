/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.test.keyvault;

import com.microsoft.azure.management.keyvault.Vault;
import com.microsoft.azure.management.resources.fluentcore.utils.SdkContext;
import com.microsoft.azure.mgmt.ConstantsHelper;
import com.microsoft.azure.test.AppRunner;
import com.microsoft.azure.mgmt.ClientSecretAccess;
import com.microsoft.azure.mgmt.KeyVaultTool;
import com.microsoft.azure.mgmt.ResourceGroupTool;
import lombok.extern.slf4j.Slf4j;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import static org.junit.Assert.assertEquals;

@Slf4j
public class KeyVaultTest {
  
    private static ClientSecretAccess access;
    private static Vault vault;
    private static String resourceGroupName;
    
    @BeforeClass
    public static void createKeyVault() {
        access = ClientSecretAccess.load();
        resourceGroupName = SdkContext.randomResourceName(ConstantsHelper.TEST_RESOURCE_GROUP_NAME_PREFIX, 30);
        final KeyVaultTool tool = new KeyVaultTool(access);
        vault = tool.createVaultInNewGroup(resourceGroupName, "test-keyvault");
        vault.secrets().define("key").withValue("value").create();
        log.info("--------------------->resources provision over");
    }
    
    @AfterClass
    public static void deleteResourceGroup() {
        final ResourceGroupTool tool = new ResourceGroupTool(access);
        tool.deleteGroup(ConstantsHelper.TEST_RESOURCE_GROUP_NAME_PREFIX);
        log.info("--------------------->resources clean over");
    }

    @Test
    public void keyVaultAsPropertySource() {
        try (AppRunner app = new AppRunner(DumbApp.class)) {
            app.property("azure.keyvault.enabled", "true");
            app.property("azure.keyvault.uri", vault.vaultUri());
            app.property("azure.keyvault.client-id", access.clientId());
            app.property("azure.keyvault.client-key", access.clientSecret());
            
            app.start();
            assertEquals("value", app.getProperty("key"));
            log.info("--------------------->test over");
        }
    }

    @Test
    public void keyVaultAsPropertySourceWithSpecificKeys() {
        try (AppRunner app = new AppRunner(DumbApp.class)) {
            app.property("azure.keyvault.enabled", "true");
            app.property("azure.keyvault.uri", vault.vaultUri());
            app.property("azure.keyvault.client-id", access.clientId());
            app.property("azure.keyvault.client-key", access.clientSecret());
            app.property("azure.keyvault.secret.keys", "key");

            app.start();
            assertEquals("value", app.getProperty("key"));
            log.info("--------------------->test over");
        }
    }


    
    @SpringBootConfiguration
    @EnableAutoConfiguration
    public static class DumbApp {}
}
