/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.gremlin;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class GremlinPropertiesUnitTest {

    @Before
    public void setup() {
        PropertiesUtil.setPropertiesWithNoTelemetry();
    }

    @Before
    public void cleanup() {
        PropertiesUtil.cleanPropertiesWithNoTelemetry();
    }

    @Test
    public void testAllProperties() {
        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

        context.register(GremlinAutoConfiguration.class);
        context.refresh();

        final GremlinProperties properties = context.getBean(GremlinProperties.class);

        Assert.assertEquals(properties.getEndpoint(), PropertiesUtil.GREMLIN_ENDPOINT);
        Assert.assertEquals(properties.getPort(), PropertiesUtil.GREMLIN_PORT);
        Assert.assertEquals(properties.getUsername(), PropertiesUtil.GREMLIN_USERNAME);
        Assert.assertEquals(properties.getPassword(), PropertiesUtil.GREMLIN_PASSWORD);
        Assert.assertEquals(String.valueOf(properties.isTelemetryAllowed()), PropertiesUtil.GREMLIN_TELEMETRYALLOWED);
    }
}
