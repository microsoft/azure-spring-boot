/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.aad;

import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
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
}
