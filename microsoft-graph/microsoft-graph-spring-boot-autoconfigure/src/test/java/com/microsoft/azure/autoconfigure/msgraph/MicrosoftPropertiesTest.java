/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.autoconfigure.msgraph;

import org.junit.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

public class MicrosoftPropertiesTest {
    @Test
    public void canSetProperties() {
        System.setProperty(Constants.APP_ID_PROPERTY, Constants.APP_ID);
        System.setProperty(Constants.APP_SCERET_PROPERTY, Constants.APP_SCERET);

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(Config.class);
            context.refresh();
            final MicrosoftProperties properties = context.getBean(MicrosoftProperties.class);

            assertThat(properties.getAppId()).isEqualTo(Constants.APP_ID);
            assertThat(properties.getAppSecret()).isEqualTo(Constants.APP_SCERET);
        }

        System.clearProperty(Constants.APP_ID_PROPERTY);
        System.clearProperty(Constants.APP_SCERET_PROPERTY);
    }

    @Configuration
    @EnableConfigurationProperties(MicrosoftProperties.class)
    static class Config {
    }
}
