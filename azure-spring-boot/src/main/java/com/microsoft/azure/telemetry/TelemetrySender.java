/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.telemetry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.spring.support.GetHashMac;
import com.microsoft.azure.utils.PropertyLoader;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON;

@Slf4j
public class TelemetrySender {

    private static final String TELEMETRY_TARGET_URL = "https://dc.services.visualstudio.com/v2/track";

    private static final String PROJECT_INFO = "spring-boot-starter/" + PropertyLoader.getProjectVersion();

    private static final int RETRY_LIMIT = 3; // Align the retry times with sdk

    private ResponseEntity<String> executeRequest(final TelemetryEventData eventData) {
        final HttpHeaders headers = new HttpHeaders();
        final ObjectMapper mapper = new ObjectMapper();

        headers.add(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON.toString());

        try {
            final RestTemplate restTemplate = new RestTemplate();
            final HttpEntity<String> body = new HttpEntity<>(mapper.writeValueAsString(eventData), headers);

            return restTemplate.exchange(TELEMETRY_TARGET_URL, HttpMethod.POST, body, String.class);
        } catch (Exception ignore) {
            log.warn("Failed to exchange telemetry request, {}.", ignore.getMessage());
        }

        return null;
    }

    private void sendTelemetryData(@NonNull TelemetryEventData eventData) {
        ResponseEntity<String> response = null;

        for (int i = 0; i < RETRY_LIMIT; i++) {
            response = executeRequest(eventData);

            if (response != null && response.getStatusCode() == HttpStatus.OK) {
                return;
            }
        }

        if (response != null && response.getStatusCode() != HttpStatus.OK) {
            log.warn("Failed to send telemetry data, response status code {}.", response.getStatusCode().toString());
        }
    }

    public void send(String name, @NonNull Map<String, String> properties) {
        Assert.hasText(name, "Event name should contain text.");

        properties.putIfAbsent(TelemetryData.INSTALLATION_ID, GetHashMac.getHashMac());
        properties.putIfAbsent(TelemetryData.PROJECT_VERSION, PROJECT_INFO);

        sendTelemetryData(new TelemetryEventData(name, properties));
    }
}

