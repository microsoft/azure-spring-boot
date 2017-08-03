/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.autoconfigure.adintegration;

import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

public class AzureADJwtFilterAutoConfigurationTest {
    @Test
    public void createAzureADJwtFilter() throws Exception {
        System.setProperty(Constants.CLIENT_ID_PROPERTY, Constants.CLIENT_ID);
        System.setProperty(Constants.CLIENT_SECRET_PROPERTY, Constants.CLIENT_SECRET);
        System.setProperty(Constants.ALLOWED_ROLES_GROUPS_PROPERTY, Constants.ALLOWED_ROLES_GROUPS.toString().replace("[", "").replace("]", ""));

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(AzureADJwtFilterAutoConfiguration.class);
            context.refresh();

            final AzureADJwtTokenFilter azureADJwtTokenFilter = context.getBean(AzureADJwtTokenFilter.class);
            assertThat(azureADJwtTokenFilter).isNotNull();
            assertThat(azureADJwtTokenFilter).isExactlyInstanceOf(AzureADJwtTokenFilter.class);
        }

        System.clearProperty(Constants.CLIENT_ID_PROPERTY);
        System.clearProperty(Constants.CLIENT_SECRET_PROPERTY);
        System.clearProperty(Constants.ALLOWED_ROLES_GROUPS_PROPERTY);
    }
}