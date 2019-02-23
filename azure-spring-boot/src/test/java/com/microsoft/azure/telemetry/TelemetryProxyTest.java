/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.telemetry;

import com.microsoft.applicationinsights.TelemetryClient;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

public class TelemetryProxyTest {

    private static final String TELEMETRY_PROPERTY = "telemetry.instrumentationKey";

    private static final String TEST_INSTRUMENTATION_KEY = "fake-key";

    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(TelemetryProxyConfiguration.class))
            .withPropertyValues(String.format("%s=%s", TELEMETRY_PROPERTY, TEST_INSTRUMENTATION_KEY));

    @Test
    public void testTelemetryProxyBean() {
        this.contextRunner.run(c -> {
            final TelemetryProxyConfiguration config = c.getBean(TelemetryProxyConfiguration.class);
            final TelemetryProxy proxy = c.getBean(TelemetryProxy.class);
            final TelemetryProperties properties = c.getBean(TelemetryProperties.class);

            assertThat(config).isNotNull();
            assertThat(proxy).isNotNull();
            assertThat(properties.getInstrumentationKey()).isEqualTo(TEST_INSTRUMENTATION_KEY);
        });
    }

    @Test
    public void testTelemetryKeyNotPolluted() {
        this.contextRunner.run(c -> {
            final TelemetryClient client = new TelemetryClient();
            assertThat(client.getContext().getInstrumentationKey()).isNotEqualTo(TEST_INSTRUMENTATION_KEY);
        });
    }
}
