/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.autoconfigure.mediaservices;

import org.junit.Test;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.validation.BindValidationException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.ObjectError;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class MediaServicesPropertiesTest {

    @Test
    public void canSetProperties() {
        System.setProperty(Constants.ACCOUNT_NAME_PROPERTY, Constants.ACCOUNT_NAME);
        System.setProperty(Constants.ACCOUNT_KEY_PROPERTY, Constants.ACCOUNT_KEY);
        System.setProperty(Constants.PROXY_HOST_PROPERTY, Constants.PROXY_HOST);
        System.setProperty(Constants.PROXY_PORT_PROPERTY, Constants.PROXY_PORT);
        System.setProperty(Constants.PROXY_SCHEME_PROPERTY, Constants.PROXY_SCHEME);

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(Config.class);
            context.refresh();

            final MediaServicesProperties properties = context.getBean(MediaServicesProperties.class);
            assertThat(properties.getAccountName()).isEqualTo(Constants.ACCOUNT_NAME);
            assertThat(properties.getAccountKey()).isEqualTo(Constants.ACCOUNT_KEY);
            assertThat(properties.getProxyHost()).isEqualTo(Constants.PROXY_HOST);
            assertThat(properties.getProxyPort()).isEqualTo(Integer.valueOf(Constants.PROXY_PORT));
            assertThat(properties.getProxyScheme()).isEqualTo(Constants.PROXY_SCHEME);
        }

        System.clearProperty(Constants.ACCOUNT_NAME_PROPERTY);
        System.clearProperty(Constants.ACCOUNT_KEY_PROPERTY);
        System.clearProperty(Constants.PROXY_HOST_PROPERTY);
        System.clearProperty(Constants.PROXY_PORT_PROPERTY);
        System.clearProperty(Constants.PROXY_SCHEME_PROPERTY);
    }

    @Test
    public void emptySettingNotAllowed() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            Exception exception = null;

            context.register(Config.class);

            try {
                context.refresh();
            } catch (Exception e) {
                exception = e;
            }

            assertThat(exception).isNotNull();
            assertThat(exception).isExactlyInstanceOf(ConfigurationPropertiesBindException.class);

            final BindValidationException bindException = (BindValidationException) exception.getCause().getCause();
            final List<ObjectError> errors = bindException.getValidationErrors().getAllErrors();
            final List<String> errorStrings = errors.stream().map(e -> e.toString()).collect(Collectors.toList());

            Collections.sort(errorStrings);

            final List<String> errorStringsExpected = Arrays.asList(
                    "Field error in object 'azure.mediaservices' on field 'accountKey': rejected value [null];",
                    "Field error in object 'azure.mediaservices' on field 'accountName': rejected value [null];"
            );

            assertThat(errorStrings.size()).isEqualTo(errorStringsExpected.size());

            for (int i = 0; i < errorStrings.size(); i++) {
                assertThat(errorStrings.get(i)).contains(errorStringsExpected.get(i));
            }
        }
    }

    @Configuration
    @EnableConfigurationProperties(MediaServicesProperties.class)
    static class Config {
    }
}

