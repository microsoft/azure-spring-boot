/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.sqlserver;

import com.microsoft.azure.telemetry.TelemetryData;
import com.microsoft.azure.telemetry.TelemetryProxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.JndiDataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.XADataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@ConditionalOnClass({DataSource.class, EmbeddedDatabaseType.class})
@EnableConfigurationProperties({DataSourceProperties.class, KeyVaultProperties.class})
@ConditionalOnProperty(name = AEConstants.PROPERTY_AE_ENABLED)
@AutoConfigureBefore({DataSourceAutoConfiguration.class, JndiDataSourceAutoConfiguration.class,
        XADataSourceAutoConfiguration.class})
public class AlwaysEncryptedAutoConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(AlwaysEncryptedAutoConfiguration.class);

    private final KeyVaultProperties properties;
    private final TelemetryProxy telemetryProxy;

    public AlwaysEncryptedAutoConfiguration(KeyVaultProperties properties) {
        this.properties = properties;
        this.telemetryProxy = new TelemetryProxy(properties.isAllowTelemetry());
    }

    private void trackCustomEvent() {
        final HashMap<String, String> customTelemetryProperties = new HashMap<>();

        final String[] packageNames = this.getClass().getPackage().getName().split("\\.");

        if (packageNames.length > 1) {
            customTelemetryProperties.put(TelemetryData.SERVICE_NAME, packageNames[packageNames.length - 1]);
        }
        telemetryProxy.trackEvent(ClassUtils.getUserClass(this.getClass()).getSimpleName(), customTelemetryProperties);
    }

    /**
     *
     * @return post processor bean that initializes KeyVault ofr SQL Driver
     */
    @Bean(name = "dataSourceKeyVaultInitializer")
    @ConditionalOnClass(com.microsoft.sqlserver.jdbc.SQLServerDriver.class)
    public BeanPostProcessor dataSourceKeyVaultInitializer() {
        trackCustomEvent();
        return new KeyVaultProviderInitializer();
    }


    @ConditionalOnClass(com.microsoft.sqlserver.jdbc.SQLServerDriver.class)
    @ConditionalOnMissingBean(JdbcDataSourcePropertiesUpdater.class)
    static class SqlServerJdbcInfoProviderConfiguration {

        @Bean
        public JdbcDataSourcePropertiesUpdater defaultSqlServerJdbcInfoProvider() {
            return new JdbcDataSourcePropertiesUpdater();
        }
    }

    @Configuration
    @Import({SqlServerJdbcInfoProviderConfiguration.class})
    static class AlwaysEncryptedDataSourcePropertiesConfiguration {

        @Bean
        @Primary
        @ConditionalOnBean(JdbcDataSourcePropertiesUpdater.class)
        public DataSourceProperties dataSourceProperties(DataSourceProperties dataSourceProperties,
                                                         JdbcDataSourcePropertiesUpdater updater) {
            LOG.info("Setting AlwaysEncrypted url");
            // Set Property to enable Encryption
           updater.updateDataSourceProperties(dataSourceProperties);

           return dataSourceProperties;
        }
    }


}
