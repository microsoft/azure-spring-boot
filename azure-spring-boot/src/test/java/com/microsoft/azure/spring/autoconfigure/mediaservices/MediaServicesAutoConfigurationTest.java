/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.autoconfigure.mediaservices;

import com.microsoft.windowsazure.services.media.MediaContract;
import com.microsoft.windowsazure.services.media.implementation.MediaExceptionProcessor;
import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

public class MediaServicesAutoConfigurationTest {
    @Test
    public void createMediaServiceAccount() {
        System.setProperty(Constants.ACCOUNT_KEY_PROPERTY, Constants.ACCOUNT_KEY);
        System.setProperty(Constants.ACCOUNT_NAME_PROPERTY, Constants.ACCOUNT_NAME);

        createAndVerifyMediaContract();

        System.clearProperty(Constants.ACCOUNT_KEY_PROPERTY);
        System.clearProperty(Constants.ACCOUNT_NAME_PROPERTY);
    }

    @Test
    public void createMediaServiceAccountWithProxy() {
        System.setProperty(Constants.ACCOUNT_KEY_PROPERTY, Constants.ACCOUNT_KEY);
        System.setProperty(Constants.ACCOUNT_NAME_PROPERTY, Constants.ACCOUNT_NAME);
        System.setProperty(Constants.PROXY_HOST_PROPERTY, Constants.PROXY_HOST);
        System.setProperty(Constants.PROXY_PORT_PROPERTY, Constants.PROXY_PORT);
        System.setProperty(Constants.PROXY_SCHEME_PROPERTY, Constants.PROXY_SCHEME);

        createAndVerifyMediaContract();

        System.clearProperty(Constants.ACCOUNT_KEY_PROPERTY);
        System.clearProperty(Constants.ACCOUNT_NAME_PROPERTY);
        System.clearProperty(Constants.PROXY_HOST_PROPERTY);
        System.clearProperty(Constants.PROXY_PORT_PROPERTY);
        System.clearProperty(Constants.PROXY_SCHEME_PROPERTY);
    }

    @Test
    public void createMediaServiceAccountWithProxyHostMissing() {
        System.setProperty(Constants.ACCOUNT_KEY_PROPERTY, Constants.ACCOUNT_KEY);
        System.setProperty(Constants.ACCOUNT_NAME_PROPERTY, Constants.ACCOUNT_NAME);
        System.setProperty(Constants.PROXY_PORT_PROPERTY, Constants.PROXY_PORT);

        createAndFailWithCause("Please Set Network Proxy host in application.properties");

        System.clearProperty(Constants.ACCOUNT_KEY_PROPERTY);
        System.clearProperty(Constants.ACCOUNT_NAME_PROPERTY);
        System.clearProperty(Constants.PROXY_PORT_PROPERTY);
    }

    @Test
    public void createMediaServiceAccountWithProxyPortMissing() {
        System.setProperty(Constants.ACCOUNT_KEY_PROPERTY, Constants.ACCOUNT_KEY);
        System.setProperty(Constants.ACCOUNT_NAME_PROPERTY, Constants.ACCOUNT_NAME);
        System.setProperty(Constants.PROXY_HOST_PROPERTY, Constants.PROXY_HOST);

        createAndFailWithCause("Please Set Network Proxy port in application.properties");

        System.clearProperty(Constants.ACCOUNT_KEY_PROPERTY);
        System.clearProperty(Constants.ACCOUNT_NAME_PROPERTY);
        System.clearProperty(Constants.PROXY_HOST_PROPERTY);
    }

    private void createAndVerifyMediaContract() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(MediaServicesAutoConfiguration.class);
            context.refresh();

            final MediaContract mediaContract = context.getBean(MediaContract.class);
            assertThat(mediaContract).isNotNull();
            assertThat(mediaContract).isExactlyInstanceOf(MediaExceptionProcessor.class);
        }
    }

    private void createAndFailWithCause(String cause) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(MediaServicesAutoConfiguration.class);

            Exception exception = null;
            try {
                context.refresh();
            } catch (Exception e) {
                exception = e;
            }

            assertThat(exception).isNotNull();
            assertThat(exception).isExactlyInstanceOf(BeanCreationException.class);
            assertThat(exception.getCause().getCause().toString()).contains(cause);
        }
    }
}
