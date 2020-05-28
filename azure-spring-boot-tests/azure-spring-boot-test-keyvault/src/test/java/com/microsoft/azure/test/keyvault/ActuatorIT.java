/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.test.keyvault;

import com.microsoft.azure.management.keyvault.Vault;
import com.microsoft.azure.management.resources.fluentcore.utils.SdkContext;
import com.microsoft.azure.mgmt.ClientSecretAccess;
import static com.microsoft.azure.mgmt.ConstantsHelper.TEST_RESOURCE_GROUP_NAME_PREFIX;
import com.microsoft.azure.mgmt.KeyVaultTool;
import com.microsoft.azure.mgmt.ResourceGroupTool;
import com.microsoft.azure.test.AppRunner;
import lombok.extern.slf4j.Slf4j;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;

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
public class ActuatorIT {

    /**
     * Stores the client secret access.
     */
    private static ClientSecretAccess access;

    /**
     * Stores the key vault prefix we use.
     */
    private static final String KEY_VAULT_PREFIX = "actuator-kv";

    /**
     * Stores the key vault secret key we use.
     */
    private static final String KEY_VAULT_SECRET_KEY = "actuator-key";

    /**
     * Stores the key vault secret value we use.
     */
    private static final String KEY_VAULT_SECRET_VALUE = "actuator-value";

    /**
     * Stores the resource group name.
     */
    private static String resourceGroupName;
    
    /**
     * Stores the REST template to use.
     */
    private static RestTemplate restTemplate;

    /**
     * Stores the key vault.
     */
    private static Vault vault;

    /**
     * After class.
     */
    @AfterClass
    public static void afterClass() {
        final ResourceGroupTool tool = new ResourceGroupTool(access);
        tool.deleteGroup(resourceGroupName);
    }

    /**
     * Before class.
     *
     * @throws Exception
     */
    @BeforeClass
    public static void beforeClass() throws Exception {
        access = ClientSecretAccess.load();
        resourceGroupName = SdkContext.randomResourceName(TEST_RESOURCE_GROUP_NAME_PREFIX, 30);
        final KeyVaultTool tool = new KeyVaultTool(access);
        vault = tool.createVaultInNewGroup(resourceGroupName, KEY_VAULT_PREFIX);
        vault.secrets().define(KEY_VAULT_SECRET_KEY).withValue(KEY_VAULT_SECRET_VALUE).create();
        restTemplate = new RestTemplate();
    }
    
    /**
     * Test the Spring Boot Health indicator integration.
     */
    @Test
    public void testSpringBootActuatorHealth() {
        try (AppRunner app = new AppRunner(ActuatorTestApp.class)) {
            app.property("azure.keyvault.enabled", "true");
            app.property("azure.keyvault.uri", vault.vaultUri());
            app.property("azure.keyvault.client-id", access.clientId());
            app.property("azure.keyvault.client-key", access.clientSecret());
            app.property("azure.keyvault.tenant-id", access.tenant());
            app.property("azure.keyvault.secret.keys", KEY_VAULT_SECRET_KEY);
            app.property("management.endpoint.health.show-details", "always");
            app.property("management.endpoints.web.exposure.include", "*");
            app.property("management.health.azure-key-vault.enabled", "true");
            app.start();
            
            final String response = restTemplate.getForObject(
                    "http://localhost:" + app.port() + "/actuator/health/keyVault", String.class);
            assertEquals("{\"status\":\"UP\"}", response);
            
            app.close();
        }
    }
    
    /**
     * Test the Spring Boot /actuator/env integration.
     */
    @Test
    public void testSpringBootActuatorEnv() {
        try (AppRunner app = new AppRunner(ActuatorTestApp.class)) {
            app.property("azure.keyvault.enabled", "true");
            app.property("azure.keyvault.uri", vault.vaultUri());
            app.property("azure.keyvault.client-id", access.clientId());
            app.property("azure.keyvault.client-key", access.clientSecret());
            app.property("azure.keyvault.tenant-id", access.tenant());
            app.property("azure.keyvault.secret.keys", KEY_VAULT_SECRET_KEY);
            app.property("management.endpoint.health.show-details", "always");
            app.property("management.endpoints.web.exposure.include", "*");
            app.property("management.health.azure-key-vault.enabled", "true");
            app.start();
            
            final String response = restTemplate.getForObject(
                    "http://localhost:" + app.port() + "/actuator/env", String.class);
            assertTrue(response.contains("azurekv"));
            
            app.close();
        }
    }
    
    @SpringBootApplication(scanBasePackages = {"com.microsoft.azure.keyvault.spring"})
    public static class ActuatorTestApp {
    }
}
