/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.aad;

import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.validation.BindValidationException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.ObjectError;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


public class AADAuthenticationFilterPropertiesTest {
    @Test
    public void canSetProperties() {
        System.setProperty(Constants.CLIENT_ID_PROPERTY, Constants.CLIENT_ID);
        System.setProperty(Constants.CLIENT_SECRET_PROPERTY, Constants.CLIENT_SECRET);
        System.setProperty(Constants.TARGETED_GROUPS_PROPERTY,
                Constants.TARGETED_GROUPS.toString().replace("[", "").replace("]", ""));

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

        System.clearProperty(Constants.CLIENT_ID_PROPERTY);
        System.clearProperty(Constants.CLIENT_SECRET_PROPERTY);
        System.clearProperty(Constants.TARGETED_GROUPS_PROPERTY);
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
            final BindValidationException bindValidationException =
                    (BindValidationException) exception.getCause().getCause();
            final List<ObjectError> errors = bindValidationException.getValidationErrors().getAllErrors();
            assertThat(errors.size()).isEqualTo(3);
            final List<String> errorStrings = errors.stream().map(e -> e.toString()).collect(Collectors.toList());
            Collections.sort(errorStrings);
            assertThat(errorStrings.get(2)).contains(
                    "Field error in object 'azure.activedirectory' on field 'clientSecret': rejected value [];");
            assertThat(errorStrings.get(1)).contains(
                    "Field error in object 'azure.activedirectory' on field 'clientId': rejected value [];");
            assertThat(errorStrings.get(0)).contains(
                    "Field error in object 'azure.activedirectory' on field 'activeDirectoryGroups': " +
                            "rejected value [null];");
        }

        System.clearProperty(Constants.CLIENT_ID_PROPERTY);
        System.clearProperty(Constants.CLIENT_SECRET_PROPERTY);
    }

    @Configuration
    @EnableConfigurationProperties(AADAuthenticationFilterProperties.class)
    static class Config {
    }
}
