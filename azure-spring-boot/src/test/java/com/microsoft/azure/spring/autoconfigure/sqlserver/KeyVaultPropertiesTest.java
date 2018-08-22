/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.autoconfigure.sqlserver;

import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

public class KeyVaultPropertiesTest {
    public static final String CLIENT_SECRET_PROPERTY = "spring.datasource.always-encrypted.keyvault.client-secret";
    public static final String CLIENT_ID_PROPERTY = "spring.datasource.always-encrypted.keyvault.client-id";


    @After
    public void clearAllProperties() {
        System.clearProperty(CLIENT_SECRET_PROPERTY);
        System.clearProperty(CLIENT_ID_PROPERTY);
    }

    @Test
    public void canSetAllProperties() {
        System.setProperty(CLIENT_SECRET_PROPERTY, "secret");
        System.setProperty(CLIENT_ID_PROPERTY, "id");

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(KeyVaultPropertiesTest.Config.class);
            context.refresh();
            final KeyVaultProperties properties = context.getBean(KeyVaultProperties.class);

            assertThat(properties.getClientId()).isEqualTo(System.getProperty(CLIENT_ID_PROPERTY));
            assertThat(properties.getClientSecret()).isEqualTo(System.getProperty(CLIENT_SECRET_PROPERTY));
         }
    }

    @Test
    public void emptySettingNotAllowed() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            Exception exception = null;

            context.register(KeyVaultPropertiesTest.Config.class);

            try {
                context.refresh();
            } catch (Exception e) {
                exception = e;
            }
            assertThat(exception).isNotNull();
            assertThat(exception).isExactlyInstanceOf(BeanCreationException.class);

            final String errorStringExpected = "spring.datasource.always-encrypted.keyvault.client-id must be provided";
            assertThat(exception.getMessage().contains(errorStringExpected));

        }
    }


    @Configuration
    @EnableConfigurationProperties(KeyVaultProperties.class)
    static class Config {
    }
}
