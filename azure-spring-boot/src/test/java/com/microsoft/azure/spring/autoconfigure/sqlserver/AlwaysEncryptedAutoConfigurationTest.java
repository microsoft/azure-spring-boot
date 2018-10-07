/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.autoconfigure.sqlserver;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static junit.framework.TestCase.assertNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

public class AlwaysEncryptedAutoConfigurationTest {

    private AnnotationConfigApplicationContext context;

    @Before
    public void setUp() {
        this.context = new AnnotationConfigApplicationContext();
    }

    @After
    public void tearDown() {
        if (this.context != null) {
            this.context.close();
        }
    }

    @Test(expected = NoSuchBeanDefinitionException.class)
    public void setDataEncryptionDisabled() {
        this.context.register(SQLServerDataSource.class);
        this.context.register(AlwaysEncryptedAutoConfiguration.class);
        EnvironmentTestUtils.addEnvironment(context, AEConstants.PROPERTY_AE_ENABLED + "=false");
        this.context.refresh();
        assertNull (context.getBean(KeyVaultProviderInitializer.class));
    }

    @Test
    public void setDataEncryptionEnabled() {
        EnvironmentTestUtils.addEnvironment(context, AEConstants.PROPERTY_AE_ENABLED + "=true");
        EnvironmentTestUtils.addEnvironment(context, KeyVaultPropertiesTest.CLIENT_ID_PROPERTY + "=id");
        EnvironmentTestUtils.addEnvironment(context, KeyVaultPropertiesTest.CLIENT_SECRET_PROPERTY + "=secret");
        EnvironmentTestUtils.addEnvironment(context, "spring.datasource.url=jdbc:sqlserver://xxx");
        this.context.register(SQLServerDataSource.class);
        this.context.register(AlwaysEncryptedAutoConfiguration.class);
        this.context.refresh();
        // assert beans exists
        assertNotNull(context.getBean(KeyVaultProviderInitializer.class));
        assertNotNull(context.getBean(KeyVaultProperties.class));
        assertNotNull(context.getBean("dataSourceProperties"));
        // assert properties
        final KeyVaultProperties properties = context.getBean(KeyVaultProperties.class);
        assertThat(properties.getClientId()).isEqualTo("id");
        assertThat(properties.getClientSecret()).isEqualTo("secret");
        final DataSourceProperties dsproperties = context.getBean(DataSourceProperties.class);
        assertThat(dsproperties.getUrl().contains(AEConstants.PROPERTY_ENCRYPTION_ENABLED_VALUE));
    }

    @Test(expected = NoSuchBeanDefinitionException.class)
    public void setDataEncryptionEnabledMissingConfig() {
        this.context.register(SQLServerDataSource.class);
        this.context.register(AlwaysEncryptedAutoConfiguration.class);
        EnvironmentTestUtils.addEnvironment(context, AEConstants.PROPERTY_AE_ENABLED + "=true");
        EnvironmentTestUtils.addEnvironment(context, KeyVaultPropertiesTest.CLIENT_ID_PROPERTY + "=id");
        this.context.refresh();
        assertNotNull(context.getBean(KeyVaultProviderInitializer.class));
    }
}
