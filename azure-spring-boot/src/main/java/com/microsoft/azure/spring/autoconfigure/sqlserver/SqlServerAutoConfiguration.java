/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.sqlserver;

import com.microsoft.azure.spring.autoconfigure.documentdb.DocumentDBProperties;
import com.microsoft.sqlserver.jdbc.SQLServerColumnEncryptionAzureKeyVaultProvider;
import com.microsoft.sqlserver.jdbc.SQLServerColumnEncryptionKeyStoreProvider;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.JndiDataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.XADataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnClass({DataSource.class, SQLServerDriver.class})
@EnableConfigurationProperties({DataSourceProperties.class, KeyVaultProperties.class})
@ConditionalOnProperty(name = "spring.datasource.dataSourceProperties.ColumnEncryptionSetting",
                       havingValue = "Enabled",
                       matchIfMissing = false)
@AutoConfigureAfter({DataSourceAutoConfiguration.class, JndiDataSourceAutoConfiguration.class,
                     XADataSourceAutoConfiguration.class})

public class SqlServerAutoConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(SqlServerAutoConfiguration.class);

    private final KeyVaultProperties properties;

    public SqlServerAutoConfiguration(KeyVaultProperties properties) {
        this.properties = properties;
    }

    @ConditionalOnClass(com.microsoft.sqlserver.jdbc.SQLServerDriver.class)
    @ConditionalOnBean(DataSource.class)
    @Bean(name = "dataSourceBeanPostProcessor")
    public BeanPostProcessor dataSourceBeanPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
                return bean;
            }

            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof DataSource) {
                    try {
                        LOG.info ("initializing DataSource AlwaysEncryption Vault provider");
                        final SQLServerColumnEncryptionAzureKeyVaultProvider akvProvider =
                                new SQLServerColumnEncryptionAzureKeyVaultProvider(properties.getClientId(),
                                                                                   properties.getClientSecret());

                        final Map<String, SQLServerColumnEncryptionKeyStoreProvider> keyStoreMap =
                                                new HashMap<String, SQLServerColumnEncryptionKeyStoreProvider>();
                        keyStoreMap.put(akvProvider.getName(), akvProvider);

                        SQLServerConnection.registerColumnEncryptionKeyStoreProviders(keyStoreMap);

                    } catch (SQLException ex) {
                        LOG.error(ex.getMessage());
                        throw new FatalBeanException(ex.getMessage());
                    }
                }
                return bean;
            }
        };
    }
}
