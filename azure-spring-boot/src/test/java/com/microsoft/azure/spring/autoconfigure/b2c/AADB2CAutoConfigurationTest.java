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

    private static final String TEST_TENANT = "https://fake-tenant";
    private static final String TEST_CLIENT_ID = "fake-client-id";
    private static final String TEST_SIGN_UP_OR_IN_NAME = "fake-sign-in-or-up";
    private static final String TEST_SIGN_UP_OR_IN_REDIRECT_URL = "https://fake-redirect-url";
    private static final String TEST_LOGOUT_SUCCESS_URL = "https://fake-logout-success-url";

    private static final String TENANT_PREFIX = String.format("%s.%s", PREFIX, "tenant");
    private static final String CLIENT_ID_PREFIX = String.format("%s.%s", PREFIX, "client-id");
    private static final String LOGOUT_SUCCESS_URL_PREFIX = String.format("%s.%s", PREFIX, LOGOUT_SUCCESS_URL);
    private static final String SIGN_UP_OR_SIGN_IN_NAME_PREFIX =
            String.format("%s.%s", PREFIX, POLICY_SIGN_UP_OR_SIGN_IN_NAME);
    private static final String SIGN_UP_OR_SIGN_IN_REDIRECT_URL_PREFIX =
            String.format("%s.%s", PREFIX, POLICY_SIGN_UP_OR_SIGN_IN_REDIRECT_URL);

    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(AADB2CAutoConfiguration.class))
            .withPropertyValues(
                    String.format("%s=%s", TENANT_PREFIX, TEST_TENANT),
                    String.format("%s=%s", CLIENT_ID_PREFIX, TEST_CLIENT_ID),
                    String.format("%s=%s", LOGOUT_SUCCESS_URL_PREFIX, TEST_LOGOUT_SUCCESS_URL),
                    String.format("%s=%s", SIGN_UP_OR_SIGN_IN_NAME_PREFIX, TEST_SIGN_UP_OR_IN_NAME),
                    String.format("%s=%s", SIGN_UP_OR_SIGN_IN_REDIRECT_URL_PREFIX, TEST_SIGN_UP_OR_IN_REDIRECT_URL)
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
            assertThat(properties.getTenant()).isEqualTo(TEST_TENANT);
            assertThat(properties.getClientId()).isEqualTo(TEST_CLIENT_ID);

            final AADB2CProperties.Policies policies = properties.getPolicies();

            assertThat(policies.getSignUpOrSignIn().getName()).isEqualTo(TEST_SIGN_UP_OR_IN_NAME);
            assertThat(policies.getSignUpOrSignIn().getRedirectURI()).isEqualTo(TEST_SIGN_UP_OR_IN_REDIRECT_URL);
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
        });
    }

    @Test
    public void testFilterBean() {
        this.contextRunner.run(c -> {
            final AADB2CFilter filter = c.getBean(AADB2CFilter.class);

            assertThat(filter).isNotNull();
        });
    }
}
