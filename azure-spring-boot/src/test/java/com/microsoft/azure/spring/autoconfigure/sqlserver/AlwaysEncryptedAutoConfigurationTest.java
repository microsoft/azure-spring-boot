/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.autoconfigure.sqlserver;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

public class AlwaysEncryptedAutoConfigurationTest {

    @After
    public void clearAllProperties() {
        System.clearProperty(KeyVaultPropertiesTest.CLIENT_SECRET_PROPERTY);
        System.clearProperty(KeyVaultPropertiesTest.CLIENT_ID_PROPERTY);
        System.clearProperty(AEConstants.PROPERTY_AE_ENABLED);
    }

    @Test
    public void setDataEncryptionDisabled() {
        System.setProperty(AEConstants.PROPERTY_AE_ENABLED, "false");

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(AlwaysEncryptedAutoConfiguration.class);
            context.register(SQLServerDataSource.class);
            context.refresh();

            BeanPostProcessor beanPostProcessor = null;
            try {
                beanPostProcessor = context.getBean("dataSourceBeanPostProcessor",
                                                     BeanPostProcessor.class);
            } catch (Exception e) {
                assertThat(e).isExactlyInstanceOf(NoSuchBeanDefinitionException.class);
            }

            assertThat(beanPostProcessor).isNull();
        }
    }

    @Test
    public void setDataEncryptionEnabled() {
        System.setProperty(AEConstants.PROPERTY_AE_ENABLED, "true");
        System.setProperty(KeyVaultPropertiesTest.CLIENT_SECRET_PROPERTY, "secret");
        System.setProperty(KeyVaultPropertiesTest.CLIENT_ID_PROPERTY, "id");

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(AlwaysEncryptedAutoConfiguration.class);
            context.register(SQLServerDataSource.class);
            context.refresh();

            final BeanPostProcessor beanPostProcessor = context.getBean("dataSourceBeanPostProcessor",
                                                                      BeanPostProcessor.class);
            assertThat(beanPostProcessor).isNotNull();
        }
    }

    @Test
    public void setDataEncryptionEnabledMissingConfig() {
        System.setProperty(AEConstants.PROPERTY_AE_ENABLED, "true");
        System.setProperty(KeyVaultPropertiesTest.CLIENT_ID_PROPERTY, "id");

        final String errorStringExpected = "spring.datasource.always-encrypted.keyvault.client-secret must be provided";

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(AlwaysEncryptedAutoConfiguration.class);
            context.register(SQLServerDataSource.class);
            try {
                context.refresh();
            } catch (Exception e) {
                assertThat(e).isExactlyInstanceOf(UnsatisfiedDependencyException.class);
                assertThat(e.getMessage().contains(errorStringExpected));
            }
        }
    }
}
