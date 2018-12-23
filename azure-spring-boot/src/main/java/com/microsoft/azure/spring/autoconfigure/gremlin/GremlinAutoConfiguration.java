/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.gremlin;

import com.microsoft.azure.telemetry.TelemetryData;
import com.microsoft.azure.telemetry.TelemetryProxy;
import com.microsoft.spring.data.gremlin.common.GremlinConfig;
import com.microsoft.spring.data.gremlin.common.GremlinFactory;
import com.microsoft.spring.data.gremlin.conversion.MappingGremlinConverter;
import com.microsoft.spring.data.gremlin.mapping.GremlinMappingContext;
import com.microsoft.spring.data.gremlin.query.GremlinTemplate;
import com.microsoft.spring.data.gremlin.telemetry.EmptyTracker;
import com.microsoft.spring.data.gremlin.telemetry.TelemetryTracker;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScanner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Persistent;
import org.springframework.lang.NonNull;
import org.springframework.util.ClassUtils;

import java.util.HashMap;

@Configuration
@ConditionalOnClass({GremlinFactory.class, GremlinTemplate.class, MappingGremlinConverter.class})
@ConditionalOnProperty(prefix = "gremlin", value = {"endpoint", "port", "username", "password"})
@EnableConfigurationProperties(GremlinProperties.class)
public class GremlinAutoConfiguration {

    private final GremlinProperties properties;

    private final TelemetryProxy telemetryProxy;

    private final ApplicationContext applicationContext;

    public GremlinAutoConfiguration(@NonNull GremlinProperties properties, @NonNull ApplicationContext context) {
        this.properties = properties;
        this.applicationContext = context;
        this.telemetryProxy = new TelemetryProxy(properties.isTelemetryAllowed());
    }

    private void trackCustomEvent() {
        final HashMap<String, String> customTelemetryProperties = new HashMap<>();
        final String[] packageNames = this.getClass().getPackage().getName().split("\\.");

        if (packageNames.length > 1) {
            customTelemetryProperties.put(TelemetryData.SERVICE_NAME, packageNames[packageNames.length - 1]);
        }

        telemetryProxy.trackEvent(ClassUtils.getUserClass(this.getClass()).getSimpleName(), customTelemetryProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public TelemetryTracker getTelemetryTracker() {
        if (getGremlinConfig().isTelemetryAllowed()) {
            return new TelemetryTracker();
        }

        return new EmptyTracker();
    }

    @Bean
    @ConditionalOnMissingBean
    public GremlinConfig getGremlinConfig() {
        return GremlinConfig.builder(properties.getEndpoint(), properties.getUsername(), properties.getPassword())
                .port(properties.getPort())
                .sslEnabled(properties.isSslEnabled())
                .telemetryAllowed(properties.isTelemetryAllowed())
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public GremlinFactory gremlinFactory() {
        this.trackCustomEvent();

        return new GremlinFactory(getGremlinConfig());
    }

    @Bean
    @ConditionalOnMissingBean
    public GremlinTemplate gremlinTemplate(GremlinFactory factory, MappingGremlinConverter converter) {
        return new GremlinTemplate(factory, converter);
    }

    @Bean
    @ConditionalOnMissingBean
    public GremlinMappingContext gremlinMappingContext() {
        try {
            final GremlinMappingContext context = new GremlinMappingContext();

            context.setInitialEntitySet(new EntityScanner(this.applicationContext).scan(Persistent.class));

            return context;
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public MappingGremlinConverter mappingGremlinConverter(GremlinMappingContext context) {
        return new MappingGremlinConverter(context);
    }
}

