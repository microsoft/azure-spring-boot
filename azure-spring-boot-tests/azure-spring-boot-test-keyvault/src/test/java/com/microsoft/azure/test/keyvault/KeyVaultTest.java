package com.microsoft.azure.test.keyvault;

import com.microsoft.azure.management.keyvault.Vault;
import com.microsoft.azure.test.AppRunner;
import com.microsoft.azure.mgmt.ClientSecretAccess;
import com.microsoft.azure.mgmt.KeyVaultTool;
import com.microsoft.azure.mgmt.ResourceGroupTool;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import static org.junit.Assert.assertEquals;

public class KeyVaultTest {
  
    private static ClientSecretAccess access;
    private static Vault vault;
    
    @BeforeClass
    public static void createKeyVault() {
        access = ClientSecretAccess.load();
        
        KeyVaultTool tool = new KeyVaultTool(access);
        vault = tool.createVaultInNewGroup("spring-boot-test-rg", "test-keyvault");      
        vault.secrets().define("key").withValue("value").create();
    }
    
    @AfterClass
    public static void deleteResourceGroup() {
        ResourceGroupTool tool = new ResourceGroupTool(access);
        tool.deleteGroup("spring-boot-test-rg");
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
        }
    }
    
    @SpringBootConfiguration
    @EnableAutoConfiguration
    public static class DumbApp {}
}
