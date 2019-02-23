/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.telemetry;

import com.microsoft.applicationinsights.TelemetryClient;
import com.microsoft.azure.spring.support.GetHashMac;
import com.microsoft.azure.utils.PropertyLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.util.Map;

@Slf4j
public class TelemetryProxy {

    private static final String PROJECT_INFO = "spring-boot-starter/" + PropertyLoader.getProjectVersion();

    private final TelemetryClient telemetryClient;

    public TelemetryProxy(String instrumentationKey) {
        this.telemetryClient = getTelemetryClient(instrumentationKey);
    }

    private TelemetryClient getTelemetryClient(String instrumentationKey) {
        if (!StringUtils.hasText(instrumentationKey)) {
            log.debug("Telemetry client instrumentation key must contain text.");
            return null;
        }

        final TelemetryClient client = new TelemetryClient();

        client.getContext().setInstrumentationKey(instrumentationKey);

        return client;
    }

    public void trackEvent(String eventName, @NonNull Map<String, String> properties) {
        if (telemetryClient != null && StringUtils.hasText(eventName)) {
            properties.putIfAbsent(TelemetryData.INSTALLATION_ID, GetHashMac.getHashMac());
            properties.putIfAbsent(TelemetryData.PROJECT_VERSION, PROJECT_INFO);

            telemetryClient.trackEvent(eventName, properties, null);
            telemetryClient.flush();
        }
    }
}
