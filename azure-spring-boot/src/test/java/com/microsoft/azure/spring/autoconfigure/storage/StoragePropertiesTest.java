/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.storage;

import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.validation.BindValidationException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.ObjectError;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class StoragePropertiesTest {

    private static final String CONNECTION_STRING = "some connection string";
    private static final String CONNECTION_STRING_PROPERTY = "azure.storage.connection-string";

    @Test
    public void canSetProperties() {
        System.setProperty(CONNECTION_STRING_PROPERTY, CONNECTION_STRING);

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(Config.class);
            context.refresh();

            final StorageProperties properties = context.getBean(StorageProperties.class);
            assertThat(properties.getConnectionString()).isEqualTo(CONNECTION_STRING);
        }

        System.clearProperty(CONNECTION_STRING_PROPERTY);
    }

    @Test
    public void emptySettingNotAllowed() {
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
            assertThat(errors.size()).isEqualTo(1);

            assertThat(errors.get(0).toString()).contains(
                    "Field error in object 'azure.storage' on field 'connectionString': " +
                            "rejected value [null];");
        }
    }

    @Configuration
    @EnableConfigurationProperties(StorageProperties.class)
    static class Config {
    }
}
