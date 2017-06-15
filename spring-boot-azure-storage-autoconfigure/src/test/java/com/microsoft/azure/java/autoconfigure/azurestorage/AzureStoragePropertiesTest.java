/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */
package com.microsoft.azure.java.autoconfigure.azurestorage;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

public class AzureStoragePropertiesTest {

    private static final String CONNECTION_STRING = "some connection string";

    @BeforeClass
    public static void beforeClass() {
        System.setProperty("azure.storage.connection-string", CONNECTION_STRING);
    }

    @Test
    public void canSetProperties() {
        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(Config.class);
        context.refresh();
        final AzureStorageProperties properties = context.getBean(AzureStorageProperties.class);

        assertThat(properties.getConnectionString()).isEqualTo(CONNECTION_STRING);
    }

    @Configuration
    @EnableConfigurationProperties(AzureStorageProperties.class)
    static class Config {
    }
}
