/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.telemetry;

import com.microsoft.applicationinsights.TelemetryClient;
import com.microsoft.azure.spring.support.GetHashMac;
import com.microsoft.azure.utils.PropertyLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TelemetryProxy {
    private static final Logger LOGGER = LoggerFactory.getLogger(TelemetryProxy.class);
    private static final String PROJECT_INFO = "spring-boot-starter/" + PropertyLoader.getProjectVersion();

    protected TelemetryClient client;
    private boolean isAllowTelemetry;

    public TelemetryProxy(boolean isAllowTelemetry) {

        this.client = new TelemetryClient();
        this.isAllowTelemetry = isAllowTelemetry;

    }

    public void trackEvent(final String eventName) {

        trackEvent(eventName, null, false);
    }

    public void trackEvent(final String eventName, final Map<String, String> customProperties) {
        trackEvent(eventName, customProperties, false);
    }

    public void trackEvent(final String eventName, final Map<String, String> customProperties,
                           final boolean overrideDefaultProperties) {

        Map<String, String> properties = getDefaultProperties();
        if (this.isAllowTelemetry) {
            properties = mergeProperties(getDefaultProperties(), customProperties,
                    overrideDefaultProperties);
        }

        try {
            client.trackEvent(eventName, properties, null);
            client.flush();
        } catch (Exception e) {
            LOGGER.trace("Failed to track event.", e);
        }
    }

    protected Map<String, String> mergeProperties(Map<String, String> defaultProperties,
                                                  Map<String, String> customProperties,
                                                  boolean overrideDefaultProperties) {
        if (customProperties == null) {
            return defaultProperties;
        }

        final Map<String, String> merged = new HashMap<>();
        if (overrideDefaultProperties) {
            merged.putAll(defaultProperties);
            merged.putAll(customProperties);
        } else {
            merged.putAll(customProperties);
            merged.putAll(defaultProperties);
        }
        final Iterator<Map.Entry<String, String>> it = merged.entrySet().iterator();
        while (it.hasNext()) {
            if (StringUtils.isEmpty(it.next().getValue())) {
                it.remove();
            }
        }
        return merged;
    }

    public Map<String, String> getDefaultProperties() {


        final Map<String, String> properties = new HashMap<>();

        if (this.isAllowTelemetry) {
            properties.put(TelemetryData.INSTALLATION_ID, GetHashMac.getHashMac());
            properties.put(TelemetryData.PROJECT_VERSION, PROJECT_INFO);
        } else {
            properties.put(TelemetryData.TELEMETRY_NOT_ALLOWED, "true");
        }
        return properties;
    }
}


