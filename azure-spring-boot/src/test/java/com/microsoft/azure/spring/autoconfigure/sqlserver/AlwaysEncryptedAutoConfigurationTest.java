/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.autoconfigure.sqlserver;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

public class AlwaysEncryptedAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
                                     .withConfiguration(AutoConfigurations.of(AlwaysEncryptedAutoConfiguration.class));

    @Test
    public void setDataEncryptionDisabled() {
        contextRunner.withPropertyValues(AEConstants.PROPERTY_AE_ENABLED + "=false")
                .withUserConfiguration(SQLServerDataSource.class)
                .run((context) ->  {
                    assertThat(context).doesNotHaveBean(KeyVaultProviderInitializer.class);
                });
    }

    @Test
    public void setDataEncryptionEnabled() {
        contextRunner.withPropertyValues(AEConstants.PROPERTY_AE_ENABLED + "=true",
                KeyVaultPropertiesTest.CLIENT_ID_PROPERTY + "=id",
                KeyVaultPropertiesTest.CLIENT_SECRET_PROPERTY + "=secret",
                "spring.datasource.url=jdbc:sqlserver://xxx")
                .withUserConfiguration(SQLServerDataSource.class)
                .run((context) ->  {
                    assertThat(context).hasSingleBean(KeyVaultProviderInitializer.class);
                    assertThat(context).hasSingleBean(KeyVaultProperties.class);
                    assertThat(context).hasBean("dataSourceProperties");
                    final KeyVaultProperties properties = context.getBean(KeyVaultProperties.class);
                    assertThat(properties.getClientId()).isEqualTo("id");
                    assertThat(properties.getClientSecret()).isEqualTo("secret");
                    final DataSourceProperties dsproperties = context.getBean(DataSourceProperties.class);
                    assertThat(dsproperties.getUrl().contains(AEConstants.PROPERTY_ENCRYPTION_ENABLED_VALUE));
                });
     }

    @Test
    public void setDataEncryptionEnabledMissingConfig() {
      contextRunner.withPropertyValues(AEConstants.PROPERTY_AE_ENABLED + "=true",
              KeyVaultPropertiesTest.CLIENT_ID_PROPERTY + "=id",
              "spring.datasource.url=jdbc:sqlserver://xxx")
            .withUserConfiguration(SQLServerDataSource.class)
            .run((context) ->  {
                assertThat(context).hasFailed();
                assertThat(context).getFailure()
                        .hasMessageContaining("always-encrypted.keyvault.client-secret must be provided");
            });
    }
}
