/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.b2c;

import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;

import static com.microsoft.azure.spring.autoconfigure.b2c.AADB2CAuthenticationProperties.*;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class AADB2CAuthenticationAutoConfigurationTest {

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
            .withConfiguration(AutoConfigurations.of(AADB2CAuthenticationAutoConfiguration.class))
            .withPropertyValues(
                    String.format("%s=%s", TENANT_PREFIX, TENANT),
                    String.format("%s=%s", CLIENT_ID_PREFIX, CLIENT_ID),
                    String.format("%s=%s", POLICY_SIGN_UP_OR_SIGN_IN_NAME_PREFIX, SIGN_UP_OR_IN_NAME),
                    String.format("%s=%s", POLICY_SIGN_UP_OR_SIGN_IN_REDIRECT_URL_PREFIX, SIGN_UP_OR_IN_REDIRECT_URL)
            );

    @Test
    public void testAutoConfigurationBeans() {
        this.contextRunner.run(c -> {
            final AADB2CAuthenticationAutoConfiguration config = c.getBean(AADB2CAuthenticationAutoConfiguration.class);

            assertThat(config).isNotNull();

            final AADB2CAuthenticationProperties properties = c.getBean(AADB2CAuthenticationProperties.class);

            assertThat(properties).isNotNull();
            assertThat(properties.getTenant()).isEqualTo(TENANT);
            assertThat(properties.getClientId()).isEqualTo(CLIENT_ID);

            final AADB2CAuthenticationProperties.Policies policies = properties.getPolicies();

            assertThat(policies.getSignUpOrSignIn().getName()).isEqualTo(SIGN_UP_OR_IN_NAME);
            assertThat(policies.getSignUpOrSignIn().getRedirectUrl()).isEqualTo(SIGN_UP_OR_IN_REDIRECT_URL);
        });
    }
}
