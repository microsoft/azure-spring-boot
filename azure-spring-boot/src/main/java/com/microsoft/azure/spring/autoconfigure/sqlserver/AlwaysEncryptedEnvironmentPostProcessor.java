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
@ConditionalOnProperty(name = AEConstants.PROPERTY_AE_ENABLED, matchIfMissing = false)
public class AlwaysEncryptedEnvironmentPostProcessor implements EnvironmentPostProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(AlwaysEncryptedEnvironmentPostProcessor.class);
    private static final String PROPERTY_SOURCE_NAME = "aeProperties";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {

        if (!environment.getProperty(AEConstants.PROPERTY_AE_ENABLED, Boolean.class, false)) {
            return;
        }
        LOGGER.debug("Setting AlwaysEncrypted settings");

        final MapPropertySource target = new MapPropertySource(PROPERTY_SOURCE_NAME, getSettingsMap(environment));
        environment.getPropertySources().addFirst(target);
    }

    public static Map<String, Object> getSettingsMap(ConfigurableEnvironment environment) {
        final Map<String, Object> map = new HashMap<String, Object>();

        // Set Property for HikariCP
        if (!environment.containsProperty(AEConstants.PROPERTY_DATASOURCE_COL_ENCRYPT)) {
            map.put(AEConstants.PROPERTY_DATASOURCE_COL_ENCRYPT, "Enabled");
        }
        // Attach property if Tomcat Pool
        final String connectionProps = environment.getProperty(AEConstants.PROPERTY_CONNECTION_COL_ENCRYPT);
        if (connectionProps == null) {
            map.put(AEConstants.PROPERTY_CONNECTION_COL_ENCRYPT,
                    AEConstants.PROPERTY_ENCRYPTION_ENABLED_VALUE);
        } else {
            map.put(AEConstants.PROPERTY_CONNECTION_COL_ENCRYPT, connectionProps + ", " +
                    AEConstants.PROPERTY_ENCRYPTION_ENABLED_VALUE);
        }
        return map;
    }
}