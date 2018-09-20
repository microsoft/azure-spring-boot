/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.keyvault.spring;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.env.MapPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class KeyVaultEnvironmentPostProcessorHelperUnitTest {

    @Autowired
    ApplicationContext context;

    private KeyVaultEnvironmentPostProcessorHelper keyVaultEnvironmentPostProcessorHelper;
    private ConfigurableEnvironment environment;

    @Before
    public void setup() {
        environment = (ConfigurableEnvironment) context.getEnvironment();
    }

    @Test
    public void testMSIAuthPropertiesNotInitialized() { 
        assertFalse("Environment should not contain MSI_ENDPOINT when not on Azure App Service environment",
        environment.containsProperty("MSI_ENDPOINT"));

        assertFalse("Environment should not contain MSI_SECRET when not on Azure App Service environment",
        environment.containsProperty("MSI_SECRET"));
    }

    @Test(expected = RuntimeException.class)
    public void testMSIAuthentication() {
        final MutablePropertySources propertySources = environment.getPropertySources();
        final Map msiProperties = new HashMap();

        msiProperties.put("MSI_ENDPOINT", "fakeendpoint");
        msiProperties.put("MSI_SECRET", "fakesecret");
        propertySources.addLast(new MapPropertySource("MSI_Properties", msiProperties));
        keyVaultEnvironmentPostProcessorHelper = 
            new KeyVaultEnvironmentPostProcessorHelper(environment);
        keyVaultEnvironmentPostProcessorHelper.addKeyVaultPropertySource();
    }  
}
