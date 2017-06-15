/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */
package com.microsoft.azure.java.autoconfigure.cosmosdb;


import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

public class AzureCosmosDbPropertiesTest {
    @BeforeClass
    public static void beforeClass() {
        PropertySettingUtil.setProperties();
    }

    @Test
    public void canSetAllProperties() {
        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(Config.class);
        context.refresh();
        final AzureCosmosDbProperties properties = context.getBean(AzureCosmosDbProperties.class);

        assertThat(properties.getUri()).isEqualTo(PropertySettingUtil.URI);
        assertThat(properties.getKey()).isEqualTo(PropertySettingUtil.KEY);
        assertThat(properties.getConsistencyLevel()).isEqualTo(PropertySettingUtil.CONSISTENCY_LEVEL);
    }

    @Configuration
    @EnableConfigurationProperties(AzureCosmosDbProperties.class)
    static class Config {
    }
}
