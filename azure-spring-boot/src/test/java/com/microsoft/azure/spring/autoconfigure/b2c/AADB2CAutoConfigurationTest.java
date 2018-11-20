/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.b2c;

import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;

import static com.microsoft.azure.spring.autoconfigure.b2c.AADB2CProperties.*;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class AADB2CAutoConfigurationTest {

    private static final String TENANT = "https://fake-tenant";
    private static final String CLIENT_ID = "fake-client-id";
    private static final String SIGN_UP_OR_IN_NAME = "fake-sign-in-or-up";
    private static final String SIGN_UP_OR_IN_REDIRECT_URL = "https://fake-redirect-url";

    private static final String TENANT_PREFIX = String.format("%s.%s", PREFIX, "tenant");
    private static final String CLIENT_ID_PREFIX = String.format("%s.%s", PREFIX, "client-id");
    private static final String POLICY_SIGN_UP_OR_SIGN_IN_NAME_PREFIX =
            String.format("%s.%s", PREFIX, POLICY_SIGN_UP_OR_SIGN_IN_NAME);
    private static final String POLICY_SIGN_UP_OR_SIGN_IN_REDIRECT_URL_PREFIX =
            String.format("%s.%s", PREFIX, POLICY_SIGN_UP_OR_SIGN_IN_REDIRECT_URL);

    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(AADB2CAutoConfiguration.class))
            .withPropertyValues(
                    String.format("%s=%s", TENANT_PREFIX, TENANT),
                    String.format("%s=%s", CLIENT_ID_PREFIX, CLIENT_ID),
                    String.format("%s=%s", POLICY_SIGN_UP_OR_SIGN_IN_NAME_PREFIX, SIGN_UP_OR_IN_NAME),
                    String.format("%s=%s", POLICY_SIGN_UP_OR_SIGN_IN_REDIRECT_URL_PREFIX, SIGN_UP_OR_IN_REDIRECT_URL)
            );

    @Test
    public void testAutoConfigurationBean() {
        this.contextRunner.run(c -> {
            final AADB2CAutoConfiguration config = c.getBean(AADB2CAutoConfiguration.class);

            assertThat(config).isNotNull();
        });
    }

    @Test
    public void testPropertiesBean() {
        this.contextRunner.run(c -> {
            final AADB2CProperties properties = c.getBean(AADB2CProperties.class);

            assertThat(properties).isNotNull();
            assertThat(properties.getTenant()).isEqualTo(TENANT);
            assertThat(properties.getClientId()).isEqualTo(CLIENT_ID);

            final AADB2CProperties.Policies policies = properties.getPolicies();

            assertThat(policies.getSignUpOrSignIn().getName()).isEqualTo(SIGN_UP_OR_IN_NAME);
            assertThat(policies.getSignUpOrSignIn().getRedirectURI()).isEqualTo(SIGN_UP_OR_IN_REDIRECT_URL);
        });
    }

    @Test
    public void testEntryPointBean() {
        this.contextRunner.run(c -> {
            final AADB2CEntryPoint entryPoint = c.getBean(AADB2CEntryPoint.class);

            assertThat(entryPoint).isNotNull();
        });
    }

    @Test
    public void testLogoutSuccessHandlerBean() {
        this.contextRunner.run(c -> {
            final AADB2CLogoutSuccessHandler handler = c.getBean(AADB2CLogoutSuccessHandler.class);

            assertThat(handler).isNotNull();

            final String newURL = "http://new-fake-url";
            handler.with(newURL);

            assertThat(handler.getLogoutSuccessURL()).isEqualTo(newURL);
        });
    }

    @Test(expected = AADB2CConfigurationException.class)
    public void testLogoutSuccessHandlerBeanException() {
        this.contextRunner.run(c -> {
            final AADB2CLogoutSuccessHandler handler = c.getBean(AADB2CLogoutSuccessHandler.class);

            handler.with("invalid-url");
        });
    }
}
