/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.sqlserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@ConditionalOnClass({DataSource.class, EmbeddedDatabaseType.class})
@EnableConfigurationProperties({DataSourceProperties.class, KeyVaultProperties.class})
@ConditionalOnProperty(name = "spring.datasource.alwaysencrypted", matchIfMissing = false)
public class AlwaysEncryptedEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlwaysEncryptedEnvironmentPostProcessor.class);

    private static final String PROPERTY_SOURCE_NAME = "aeProperties";
    private static final String PROPERTY_AE_ENABLED = "spring.datasource.alwaysencrypted";
    private static final String PROPERTY_COL_ENCRYPT = "spring.datasource.dataSourceProperties.ColumnEncryptionSetting";
    private static final String PROPERTY_CONNECTION = "spring.datasource.connectionProperties";


    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {

        if (!environment.getProperty(PROPERTY_AE_ENABLED, Boolean.class, false)) {
            return;
        }

        LOGGER.debug("Setting AlwaysEncrypted settings");
        final Map<String, Object> map = new HashMap<String, Object>();

        // Set Property for HikariCP
        if (environment.getProperty(PROPERTY_COL_ENCRYPT) == null) {
            map.put(PROPERTY_COL_ENCRYPT, "Enabled");
        }
        // Attach property if Tomcat Pool
        final String connectionProps = environment.getProperty(PROPERTY_CONNECTION);
        if (connectionProps == null) {
            map.put(PROPERTY_CONNECTION, "ColumnEncryptionSetting=Enabled");
        } else {
            map.put(PROPERTY_CONNECTION, connectionProps + ", ColumnEncryptionSetting=Enabled");
        }
        final MapPropertySource target = new MapPropertySource(PROPERTY_SOURCE_NAME, map);
        environment.getPropertySources().addFirst(target);

    }


}
