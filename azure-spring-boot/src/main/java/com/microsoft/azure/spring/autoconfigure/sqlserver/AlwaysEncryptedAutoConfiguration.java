/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.sqlserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.JndiDataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.XADataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

@Configuration
@ConditionalOnClass({DataSource.class, EmbeddedDatabaseType.class})
@EnableConfigurationProperties({AlwaysEncryptedDataSourceProperties.class, KeyVaultProperties.class})
@ConditionalOnProperty(name = AEConstants.PROPERTY_AE_ENABLED)
@AutoConfigureBefore({DataSourceAutoConfiguration.class, JndiDataSourceAutoConfiguration.class,
        XADataSourceAutoConfiguration.class})
public class AlwaysEncryptedAutoConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(AlwaysEncryptedAutoConfiguration.class);

    /**
     *
     * @return post processor bean that initializes KeyVault ofr SQL Driver
     */
    @Bean(name = "dataSourceKeyVaultInitializer")
    @ConditionalOnClass(com.microsoft.sqlserver.jdbc.SQLServerDriver.class)
    public KeyVaultProviderInitializer dataSourceKeyVaultInitializer(KeyVaultProperties properties) {
        return new KeyVaultProviderInitializer(properties);
    }

    @Configuration
    static class AlwaysEncryptedDataSourcePropertiesConfiguration {

        @Bean
        @Primary
        @ConditionalOnClass(com.microsoft.sqlserver.jdbc.SQLServerDriver.class)
        public DataSourceProperties dataSourceProperties() {
            LOG.info("Setting AlwaysEncrypted url flag");
            // Set Property to enable Encryption
           return new AlwaysEncryptedDataSourceProperties();
        }
    }
}
