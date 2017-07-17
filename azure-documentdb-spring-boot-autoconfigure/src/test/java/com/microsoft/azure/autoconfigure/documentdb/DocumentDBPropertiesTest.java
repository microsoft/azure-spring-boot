/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.autoconfigure.documentdb;


import org.junit.Test;
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
        }

        PropertySettingUtil.unsetProperties();
    }

    @Test
    public void shouldAllBeNullIfNotSet() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(Config.class);
            context.refresh();
            final DocumentDBProperties properties = context.getBean(DocumentDBProperties.class);

            assertThat(properties.getUri()).isNull();
            assertThat(properties.getKey()).isNull();
            assertThat(properties.getConsistencyLevel()).isNull();
        }
    }

    @Configuration
    @EnableConfigurationProperties(DocumentDBProperties.class)
    static class Config {
    }
}
