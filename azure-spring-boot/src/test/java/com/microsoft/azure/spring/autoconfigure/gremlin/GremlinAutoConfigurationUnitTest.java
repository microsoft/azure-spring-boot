/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.gremlin;

import com.microsoft.spring.data.gremlin.common.GremlinFactory;
import com.microsoft.spring.data.gremlin.conversion.MappingGremlinConverter;
import com.microsoft.spring.data.gremlin.mapping.GremlinMappingContext;
import com.microsoft.spring.data.gremlin.query.GremlinTemplate;
import org.apache.tinkerpop.gremlin.driver.Client;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class GremlinAutoConfigurationUnitTest {

    @Before
    public void setup() {
        PropertiesUtil.setProperties();
    }

    @Before
    public void cleanup() {
        PropertiesUtil.cleanProperties();
    }

    @Test
    public void testAllBeanCreated() {
        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

        context.register(GremlinAutoConfiguration.class);
        context.refresh();

        final GremlinFactory factory = context.getBean(GremlinFactory.class);
        Assert.assertNotNull(factory);

        final Client client = factory.getGremlinClient();
        Assert.assertNotNull(client);

        final GremlinTemplate template = context.getBean(GremlinTemplate.class);
        Assert.assertNotNull(template);

        final GremlinMappingContext mappingContext = context.getBean(GremlinMappingContext.class);
        Assert.assertNotNull(mappingContext);

        final MappingGremlinConverter converter = context.getBean(MappingGremlinConverter.class);
        Assert.assertNotNull(converter);

        Assert.assertEquals(template.getMappingConverter(), converter);
        Assert.assertEquals(converter.getMappingContext(), mappingContext);
    }
}
