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
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class KeyVaultPropertiesTest {
    public static final String CLIENT_SECRET_PROPERTY = "spring.datasource.always-encrypted.keyvault.client-secret";
    public static final String CLIENT_ID_PROPERTY = "spring.datasource.always-encrypted.keyvault.client-id";

    private  AnnotationConfigApplicationContext context;

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

    @Test
    public void canSetAllProperties() {
        EnvironmentTestUtils.addEnvironment(context, KeyVaultPropertiesTest.CLIENT_ID_PROPERTY + "=id");
        EnvironmentTestUtils.addEnvironment(context, KeyVaultPropertiesTest.CLIENT_SECRET_PROPERTY + "=secret");
        this.context.register(SQLServerDataSource.class);
        this.context.register(AlwaysEncryptedAutoConfiguration.class);
        this.context.register(KeyVaultPropertiesTest.Config.class);
        this.context.refresh();
        // assert beans exists
        assertNotNull(context.getBean(KeyVaultProperties.class));
        // asset properties
        final KeyVaultProperties properties = context.getBean(KeyVaultProperties.class);
        assertEquals(properties.getClientId(), "id");
        assertEquals(properties.getClientSecret(), "secret");
    }

    @Test(expected = BeanCreationException.class)
    public void emptySettingNotAllowed() {
        this.context.register(SQLServerDataSource.class);
        this.context.register(AlwaysEncryptedAutoConfiguration.class);
        this.context.register(KeyVaultPropertiesTest.Config.class);
        this.context.refresh();
        // assert beans exists
        assertNotNull(context.getBean(KeyVaultProperties.class));
    }

    @Configuration
    @EnableConfigurationProperties(KeyVaultProperties.class)
    static class Config {
    }
}
