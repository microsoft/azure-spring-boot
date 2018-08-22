/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.autoconfigure.sqlserver;

import org.junit.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

public class KeyVaultPropertiesTest {
    public static final String CLIENT_SECRET_PROPERTY = "spring.datasource.always-encrypted.keyvault.client-secret";
    public static final String CLIENT_ID_PROPERTY = "spring.datasource.always-encrypted.keyvault.client-id";

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();

    @Test
    public void canSetAllProperties() {
         this.contextRunner.withPropertyValues(CLIENT_SECRET_PROPERTY + "=secret", CLIENT_ID_PROPERTY + "=id")
            .withUserConfiguration(KeyVaultPropertiesTest.Config.class)
            .run((context) -> {
                assertThat(context).hasSingleBean(KeyVaultProperties.class);
                final KeyVaultProperties properties = context.getBean(KeyVaultProperties.class);
                assertThat(properties.getClientId()).isEqualTo("id");
                assertThat(properties.getClientSecret()).isEqualTo("secret");
            });
    }

    @Test
    public void emptySettingNotAllowed() {
        this.contextRunner.withUserConfiguration(KeyVaultPropertiesTest.Config.class)
                .run((context) -> {
                    assertThat(context).hasFailed();
                    assertThat(context).getFailure()
                            .hasMessageContaining(".always-encrypted.keyvault.client-id must be provided");
                });
     }


    @Configuration
    @EnableConfigurationProperties(KeyVaultProperties.class)
    static class Config {
    }
}
