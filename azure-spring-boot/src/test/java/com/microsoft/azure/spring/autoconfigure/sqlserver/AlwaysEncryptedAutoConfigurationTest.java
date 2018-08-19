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
    private static final String ENCRYPTION_PROPERTY = "spring.datasource.alwaysencrypted";
    private static final String CLIENT_SECRET_PROPERTY = "azure.sqlserver.keyvault.client-secret";
    private static final String CLIENT_ID_PROPERTY = "azure.sqlserver.keyvault.client-id";

    @After
    public void clearAllProperties() {
        System.clearProperty(CLIENT_SECRET_PROPERTY);
        System.clearProperty(CLIENT_ID_PROPERTY);
        System.clearProperty(ENCRYPTION_PROPERTY);
    }

    @Test
    public void setDataEncryptionDisabled() {
        System.setProperty(ENCRYPTION_PROPERTY, "false");

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
        System.setProperty(ENCRYPTION_PROPERTY, "true");
        System.setProperty(CLIENT_SECRET_PROPERTY, "secret");
        System.setProperty(CLIENT_ID_PROPERTY, "id");


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
        System.setProperty(ENCRYPTION_PROPERTY, "true");
        System.setProperty(CLIENT_ID_PROPERTY, "id");

        final String errorStringExpected = "azure.sqlserver.keyvault.client-secret must be provided";


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
