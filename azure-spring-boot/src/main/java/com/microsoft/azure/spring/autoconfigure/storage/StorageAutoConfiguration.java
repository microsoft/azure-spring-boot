/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.autoconfigure.storage;

import com.microsoft.azure.storage.blob.*;
import com.microsoft.azure.telemetry.TelemetryData;
import com.microsoft.azure.telemetry.TelemetryProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.util.HashMap;

@Configuration
@ConditionalOnClass(ServiceURL.class)
@EnableConfigurationProperties(StorageProperties.class)
@ConditionalOnProperty(prefix = "azure.storage", value = {"account-name", "account-key"})
public class StorageAutoConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(StorageAutoConfiguration.class);
    private static final String BLOB_URL = "http://%s.blob.core.windows.net";
    private static final String USER_AGENT_PREFIX = "spring-storage/";

    private final StorageProperties properties;
    private final TelemetryProxy telemetryProxy;

    public StorageAutoConfiguration(StorageProperties properties) {
        this.properties = properties;
        this.telemetryProxy = new TelemetryProxy(properties.isAllowTelemetry());
    }

    /**
     * @param options PipelineOptions bean, not required.
     * @return
     */
    @Bean
    public ServiceURL createServiceUrl(@Autowired(required = false) PipelineOptions options) throws InvalidKeyException,
            MalformedURLException {
        LOG.debug("Creating ServiceURL bean...");
        trackCustomEvent();

        final SharedKeyCredentials credentials = new SharedKeyCredentials(properties.getAccountName(),
                properties.getAccountKey());
        final URL blobUrl = new URL(String.format(BLOB_URL, properties.getAccountName()));
        final PipelineOptions pipelineOptions = buildOptions(options);
        final ServiceURL serviceURL = new ServiceURL(blobUrl, StorageURL.createPipeline(credentials, pipelineOptions));

        return serviceURL;
    }

    private PipelineOptions buildOptions(PipelineOptions fromOptions) {
        final PipelineOptions pipelineOptions = fromOptions == null ? new PipelineOptions() : fromOptions;

        pipelineOptions.withTelemetryOptions(new TelemetryOptions(USER_AGENT_PREFIX
                + pipelineOptions.telemetryOptions().userAgentPrefix()));

        return pipelineOptions;
    }

    @Bean
    @ConditionalOnProperty(prefix = "azure.storage", value = "container-name")
    public ContainerURL createContainerURL(ServiceURL serviceURL) {
        return serviceURL.createContainerURL(properties.getContainerName());
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
