/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.boot.autoconfigure.documentdb;


import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

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
            assertThat(exception.getCause().getMessage()).contains(
                    "Field error in object 'azure.documentdb' on field 'uri': rejected value [null];");
            assertThat(exception.getCause().getMessage()).contains(
                    "Field error in object 'azure.documentdb' on field 'key': rejected value [null];");
        }
    }

    @Configuration
    @EnableConfigurationProperties(DocumentDBProperties.class)
    static class Config {
    }
}
