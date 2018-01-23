/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.aad;

import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

public class AADAuthenticationFilterPropertiesTest {
    @After
    public void clearAllProperties() {
        System.clearProperty(Constants.SERVICE_ENVIRONMENT_PROPERTY);
        System.clearProperty(Constants.CLIENT_ID_PROPERTY);
        System.clearProperty(Constants.CLIENT_SECRET_PROPERTY);
        System.clearProperty(Constants.TARGETED_GROUPS_PROPERTY);
    }

    @Test
    public void canSetProperties() {
        configureAllRequiredProperties();

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(Config.class);
            context.refresh();

            final AADAuthenticationFilterProperties properties =
                    context.getBean(AADAuthenticationFilterProperties.class);

            assertThat(properties.getClientId()).isEqualTo(Constants.CLIENT_ID);
            assertThat(properties.getClientSecret()).isEqualTo(Constants.CLIENT_SECRET);
            assertThat(properties.getActiveDirectoryGroups()
                    .toString()).isEqualTo(Constants.TARGETED_GROUPS.toString());
        }
    }

    @Test
    public void defaultEnvironmentIsGlobal() {
        configureAllRequiredProperties();
        assertThat(System.getProperty(Constants.SERVICE_ENVIRONMENT_PROPERTY)).isNullOrEmpty();

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(Config.class);
            context.refresh();

            final AADAuthenticationFilterProperties properties =
                    context.getBean(AADAuthenticationFilterProperties.class);

            assertThat(properties.getEnvironment()).isEqualTo(Constants.DEFAULT_ENVIRONMENT);
        }
    }

    private void configureAllRequiredProperties(){
        System.setProperty(Constants.CLIENT_ID_PROPERTY, Constants.CLIENT_ID);
        System.setProperty(Constants.CLIENT_SECRET_PROPERTY, Constants.CLIENT_SECRET);
        System.setProperty(Constants.TARGETED_GROUPS_PROPERTY,
                Constants.TARGETED_GROUPS.toString().replace("[", "").replace("]", ""));
    }

    @Test
    public void emptySettingsNotAllowed() {
        System.setProperty(Constants.CLIENT_ID_PROPERTY, "");
        System.setProperty(Constants.CLIENT_SECRET_PROPERTY, "");

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(Config.class);

            Exception exception = null;
            try {
                context.refresh();
            } catch (Exception e) {
                exception = e;
            }

            assertThat(exception).isNotNull();
            assertThat(exception).isExactlyInstanceOf(BeanCreationException.class);
            assertThat(exception.getCause().getMessage()).contains(
                    "Field error in object " +
                            "'azure.activedirectory' on field 'clientId': rejected value []");
            assertThat(exception.getCause().getMessage()).contains(
                    "Field error in object " +
                            "'azure.activedirectory' on field 'clientSecret': rejected value []");
            assertThat(exception.getCause().getMessage()).contains(
                    "Field error in object " +
                            "'azure.activedirectory' on field 'activeDirectoryGroups': rejected value [null]");
        }
    }

    @Configuration
    @EnableConfigurationProperties(AADAuthenticationFilterProperties.class)
    static class Config {
    }
}
