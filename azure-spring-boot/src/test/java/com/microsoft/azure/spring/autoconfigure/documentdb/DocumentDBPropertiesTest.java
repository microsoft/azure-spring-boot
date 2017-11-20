/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.documentdb;


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

public class DocumentDBPropertiesTest {
    @Test
    public void canSetAllProperties() {
        PropertySettingUtil.setProperties();

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(Config.class);
            context.refresh();
            final DocumentDBProperties properties = context.getBean(DocumentDBProperties.class);

            assertThat(properties.getUri()).isEqualTo(PropertySettingUtil.URI);
            assertThat(properties.getKey()).isEqualTo(PropertySettingUtil.KEY);
            assertThat(properties.getConsistencyLevel()).isEqualTo(PropertySettingUtil.CONSISTENCY_LEVEL);
            assertThat(properties.isAllowTelemetry()).isEqualTo(PropertySettingUtil.ALLOW_TELEMETRY_TRUE);
        }

        PropertySettingUtil.unsetProperties();
    }

    @Test
    public void canSetAllowTelemetryFalse() {
        PropertySettingUtil.setAllowTelemetryFalse();

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(Config.class);
            context.refresh();
            final DocumentDBProperties properties = context.getBean(DocumentDBProperties.class);

            assertThat(properties.isAllowTelemetry()).isEqualTo(PropertySettingUtil.ALLOW_TELEMETRY_FALSE);
        }

        PropertySettingUtil.unsetProperties();
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
            assertThat(errors.size()).isEqualTo(2);
            final List<String> errorStrings = errors.stream().map(e -> e.toString()).collect(Collectors.toList());
            Collections.sort(errorStrings);
            assertThat(errorStrings.get(1)).contains(
                    "Field error in object 'azure.documentdb' on field 'uri': rejected value [null];");
            assertThat(errorStrings.get(0)).contains(
                    "Field error in object 'azure.documentdb' on field 'key': rejected value [null];");
        }
    }

    @Configuration
    @EnableConfigurationProperties(DocumentDBProperties.class)
    static class Config {
    }
}
