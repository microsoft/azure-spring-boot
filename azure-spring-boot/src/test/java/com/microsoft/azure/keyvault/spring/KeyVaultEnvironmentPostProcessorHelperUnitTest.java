/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.keyvault.spring;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.core.env.EnvironmentCapable;

import static org.junit.Assert.assertFalse;

@RunWith(SpringJUnit4ClassRunner.class)
public class KeyVaultEnvironmentPostProcessorHelperUnitTest {

    @Autowired
    ApplicationContext context;

    @Test
    public void testMSIAuthPropertiesNotInitialized() {
        final MutablePropertySources sources =
        ((ConfigurableEnvironment) context.getEnvironment()).getPropertySources();
    
        assertFalse("PropertySources should not contain MSI_ENDPOINT when not on Azure environment",
        sources.contains("MSI_ENDPOINT"));

        assertFalse("PropertySources should not contain MSI_SECRET when not on Azure environment",
        sources.contains("MSI_SECRET"));
    }  
}
