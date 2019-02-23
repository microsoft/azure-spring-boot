/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.sqlserver;

import com.microsoft.azure.telemetry.TelemetryProxy;
import com.microsoft.sqlserver.jdbc.SQLServerColumnEncryptionAzureKeyVaultProvider;
import com.microsoft.sqlserver.jdbc.SQLServerColumnEncryptionKeyStoreProvider;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.FatalBeanException;
import org.springframework.util.ClassUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static com.microsoft.azure.telemetry.TelemetryData.SERVICE_NAME;
import static com.microsoft.azure.telemetry.TelemetryData.getClassPackageSimpleName;

public class KeyVaultProviderInitializer {
    private static final Logger LOG = LoggerFactory.getLogger(KeyVaultProviderInitializer.class);

    private KeyVaultProperties properties;
    private final TelemetryProxy telemetryProxy;

    public KeyVaultProviderInitializer(KeyVaultProperties properties, TelemetryProxy telemetryProxy) {
        this.properties = properties;
        this.telemetryProxy = telemetryProxy;
        init();
    }

    public void init() {
        LOG.info("initializing DataSource AlwaysEncryption Vault provider");
        trackCustomEvent();
        try {

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

    private void trackCustomEvent() {
        if (properties.isAllowTelemetry()) {
            final HashMap<String, String> events = new HashMap<>();

            events.put(SERVICE_NAME, getClassPackageSimpleName(KeyVaultProviderInitializer.class));

            telemetryProxy.trackEvent(ClassUtils.getUserClass(this.getClass()).getSimpleName(), events);
        }
    }
}
