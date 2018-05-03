/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.autoconfigure.storage;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.telemetry.TelemetryData;
import com.microsoft.azure.telemetry.TelemetryProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.util.ClassUtils;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.HashMap;

@Configuration
@ConditionalOnMissingBean(CloudStorageAccount.class)
@EnableConfigurationProperties(StorageProperties.class)
@ConditionalOnProperty(prefix = "azure.storage", value = "connection-string")
public class StorageAutoConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(StorageAutoConfiguration.class);

    private final StorageProperties properties;
    private final TelemetryProxy telemetryProxy;

    public StorageAutoConfiguration(StorageProperties properties) {
        this.properties = properties;
        this.telemetryProxy = new TelemetryProxy(properties.isAllowTelemetry());
    }

    /**
     * Declare CloudStorageAccount bean.
     *
     * @return CloudStorageAccount bean
     */
    @Bean
    @Scope("prototype")
    public CloudStorageAccount cloudStorageAccount() throws URISyntaxException, InvalidKeyException {
        LOG.debug("cloudStorageAccount called");
        trackCustomEvent();
        return createCloudStorageAccount();
    }

    /**
     * Helper function for creating CloudStorageAccount instance from storage connection string.
     *
     * @return CloudStorageAccount object
     */
    private CloudStorageAccount createCloudStorageAccount() throws URISyntaxException, InvalidKeyException {
        LOG.debug("createCloudStorageAccount called");
        return CloudStorageAccount.parse(properties.getConnectionString());
    }

    private void trackCustomEvent() {
        final HashMap<String, String> customTelemetryProperties = new HashMap<>();
        final String[] packageNames = this.getClass().getPackage().getName().split("\\.");

        if (packageNames.length > 1) {
            customTelemetryProperties.put(TelemetryData.SERVICE_NAME, packageNames[packageNames.length - 1]);
        }
        telemetryProxy.trackEvent(ClassUtils.getUserClass(this.getClass()).getSimpleName(), customTelemetryProperties);
    }
}
