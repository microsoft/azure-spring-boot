/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.sqlserver;

import com.microsoft.sqlserver.jdbc.SQLServerColumnEncryptionAzureKeyVaultProvider;
import com.microsoft.sqlserver.jdbc.SQLServerColumnEncryptionKeyStoreProvider;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class KeyVaultProviderPostProcesor implements BeanPostProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(KeyVaultProviderPostProcesor.class);

    @Autowired
    private KeyVaultProperties properties;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        if (bean instanceof DataSource) {
            try {

                LOG.info("initializing DataSource AlwaysEncryption Vault provider");
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
}
