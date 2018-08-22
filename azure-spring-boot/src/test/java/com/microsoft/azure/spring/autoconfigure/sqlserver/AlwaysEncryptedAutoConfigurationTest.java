/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.autoconfigure.sqlserver;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

public class AlwaysEncryptedAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
                                     .withConfiguration(AutoConfigurations.of(AlwaysEncryptedAutoConfiguration.class));

    @Test
    public void setDataEncryptionDisabled() {
        contextRunner.withPropertyValues(AEConstants.PROPERTY_AE_ENABLED + "=false")
                .withUserConfiguration(SQLServerDataSource.class)
                .run((context) ->  {
                    assertThat(context).doesNotHaveBean(KeyVaultProviderPostProcesor.class);
                });
    }

    @Test
    public void setDataEncryptionEnabled() {
        contextRunner.withPropertyValues(AEConstants.PROPERTY_AE_ENABLED + "=true",
                KeyVaultPropertiesTest.CLIENT_ID_PROPERTY + "=id",
                KeyVaultPropertiesTest.CLIENT_SECRET_PROPERTY + "=secret")
                .withUserConfiguration(SQLServerDataSource.class)
                .run((context) ->  {
                    assertThat(context).hasSingleBean(KeyVaultProviderPostProcesor.class);
                    assertThat(context).hasSingleBean(KeyVaultProperties.class);
                    final KeyVaultProperties properties = context.getBean(KeyVaultProperties.class);
                    assertThat(properties.getClientId()).isEqualTo("id");
                    assertThat(properties.getClientSecret()).isEqualTo("secret");
                });
     }

    @Test
    public void setDataEncryptionEnabledMissingConfig() {
      contextRunner.withPropertyValues(AEConstants.PROPERTY_AE_ENABLED + "=true",
                                    KeyVaultPropertiesTest.CLIENT_ID_PROPERTY + "=id")
            .withUserConfiguration(SQLServerDataSource.class)
            .run((context) ->  {
                assertThat(context).hasFailed();
                assertThat(context).getFailure()
                        .hasMessageContaining("always-encrypted.keyvault.client-secret must be provided");
            });
    }
}
