package com.microsoft.azure.keyvault.spring.boot;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(initializers = KeyVaultPropertyInitializer.class)
@TestPropertySource(locations = "classpath:application.properties")
public class InitializerTest {

    @Autowired
    ApplicationContext context;

    @Test
    public void TestAzureKvPropertySourceInitialized() {
        final MutablePropertySources sources =
                ((ConfigurableEnvironment) context.getEnvironment()).getPropertySources();

        assertTrue("PropertySources should contains azurekv when enabled=false", sources.contains("azurekv"));
    }
}
