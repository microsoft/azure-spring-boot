/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.aad;

import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.core.env.Environment;

import static org.assertj.core.api.Assertions.assertThat;

public class AADAuthenticationAutoConfigurationTest {
    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(AADAuthenticationFilterAutoConfiguration.class))
            .withPropertyValues("azure.activedirectory.client-id=fake-client-id",
                    "azure.activedirectory.client-secret=fake-client-secret",
                    "azure.activedirectory.active-directory-groups=fake-group",
                    "azure.service.endpoints.global.aadKeyDiscoveryUri=http://fake.aad.discovery.uri");

    @Test
    public void createAADAuthenticationFilter() {
        this.contextRunner.run(context -> {
            final AADAuthenticationFilter azureADJwtTokenFilter = context.getBean(AADAuthenticationFilter.class);
            assertThat(azureADJwtTokenFilter).isNotNull();
            assertThat(azureADJwtTokenFilter).isExactlyInstanceOf(AADAuthenticationFilter.class);
        });
    }

    @Test
    public void serviceEndpointsCanBeOverriden() {
        this.contextRunner.withPropertyValues("azure.service.endpoints.global.aadKeyDiscoveryUri=https://test/",
                "azure.service.endpoints.global.aadSigninUri=https://test/",
                "azure.service.endpoints.global.aadGraphApiUri=https://test/",
                "azure.service.endpoints.global.aadKeyDiscoveryUri=https://test/",
                "azure.service.endpoints.global.aadMembershipRestUri=https://test/")
                .run(context -> {
                    final Environment environment = context.getEnvironment();
                    assertThat(environment.getProperty("azure.service.endpoints.global.aadSigninUri"))
                    .isEqualTo("https://test/");
                    assertThat(environment.getProperty("azure.service.endpoints.global.aadGraphApiUri"))
                            .isEqualTo("https://test/");
                    assertThat(environment.getProperty("azure.service.endpoints.global.aadKeyDiscoveryUri"))
                            .isEqualTo("https://test/");
                    assertThat(environment.getProperty("azure.service.endpoints.global.aadMembershipRestUri"))
                            .isEqualTo("https://test/");

        });
    }
}
