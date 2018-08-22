/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.sqlserver;

import org.junit.Test;
import org.mockito.Mock;
import org.springframework.core.env.ConfigurableEnvironment;
import java.util.Map;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;


public class AlwaysEncryptedPostProcessorTest {

    @Mock ConfigurableEnvironment environment;

    @Test
    public void dataSourcePropertiesEncryptionEnabled() {
        environment = mock (ConfigurableEnvironment.class);
        when(environment.getProperty(AEConstants.PROPERTY_DATASOURCE_COL_ENCRYPT)).thenReturn(null);
        when(environment.getProperty(AEConstants.PROPERTY_CONNECTION_COL_ENCRYPT)).thenReturn(null);

        final Map<String, Object> map = AlwaysEncryptedEnvironmentPostProcessor.getSettingsMap(environment);
        assertThat(map.entrySet().size()).isEqualTo(2);
        assertThat((String) map.get(AEConstants.PROPERTY_DATASOURCE_COL_ENCRYPT)).contains("Enabled");
        assertThat((String) map.get(AEConstants.PROPERTY_CONNECTION_COL_ENCRYPT))
                .contains("ColumnEncryptionSetting=Enabled");
    }

    @Test
    public void connectionPropertiesEncryptionEnabled() {
        environment = mock (ConfigurableEnvironment.class);
        when(environment.getProperty(AEConstants.PROPERTY_DATASOURCE_COL_ENCRYPT)).thenReturn(null);
        when(environment.getProperty(AEConstants.PROPERTY_CONNECTION_COL_ENCRYPT)).thenReturn("idleTime=10");

        final Map<String, Object> map = AlwaysEncryptedEnvironmentPostProcessor.getSettingsMap(environment);
        assertThat(map.entrySet().size()).isEqualTo(2);
        assertThat((String) map.get(AEConstants.PROPERTY_DATASOURCE_COL_ENCRYPT)).contains("Enabled");
        assertThat((String) map.get(AEConstants.PROPERTY_CONNECTION_COL_ENCRYPT))
                .contains(AEConstants.PROPERTY_ENCRYPTION_ENABLED_VALUE);
        assertThat((String) map.get(AEConstants.PROPERTY_CONNECTION_COL_ENCRYPT)).contains("idleTime=10");
    }
}
