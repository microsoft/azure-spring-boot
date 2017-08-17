/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.autoconfigure.msgraph;

import com.microsoft.azure.msgraph.api.Microsoft;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.boot.autoconfigure.social.SocialWebAutoConfiguration;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class MicrosoftAutoConfigurationTest {
    @Test
    public void canAutowire() {
        System.setProperty(Constants.APP_ID_PROPERTY, Constants.APP_ID);
        System.setProperty(Constants.APP_SECRET_PROPERTY, Constants.APP_SECRET);

        try (AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext()) {
            context.register(MicrosoftAutoConfiguration.class);
            context.register(SocialWebAutoConfiguration.class);
            context.refresh();
            Assertions.assertThat(context.getBean(Microsoft.class)).isNotNull();
        }

        System.clearProperty(Constants.APP_ID_PROPERTY);
        System.clearProperty(Constants.APP_SECRET_PROPERTY);
    }

    @Test
    public void cannotAutowire() {
        try (AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext()) {
            context.register(MicrosoftAutoConfiguration.class);
            context.register(SocialWebAutoConfiguration.class);
            context.refresh();

            Microsoft microsoft = null;
            try {
                microsoft = context.getBean(Microsoft.class);
            } catch (Exception e) {
                assertThat(e.getMessage()).contains("No qualifying bean of type 'com.microsoft.azure." +
                        "msgraph.api.Microsoft' available");
            }
            assertThat(microsoft).isNull();
        }
    }
}
