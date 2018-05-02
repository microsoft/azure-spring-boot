/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.aad;

import com.microsoft.azure.telemetry.TelemetryProxy;
import com.microsoft.azure.utils.PropertyLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.util.ClassUtils;

@Configuration
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = "azure.activedirectory", value = {"client-id", "client-secret"})
@EnableConfigurationProperties({AADAuthenticationFilterProperties.class, ServiceEndpointsProperties.class})
public class AADAuthenticationFilterAutoConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(AADAuthenticationFilterProperties.class);
    private static final String USER_AGENT_SUFFIX = "spring-boot-starter/" + PropertyLoader.getProjectVersion();

    private final AADAuthenticationFilterProperties aadAuthFilterProperties;
    private final ServiceEndpointsProperties serviceEndpointsProperties;

    private final TelemetryProxy telemetryProxy;

    public AADAuthenticationFilterAutoConfiguration(AADAuthenticationFilterProperties aadAuthFilterProperties,
                                                    ServiceEndpointsProperties serviceEndpointsProperties) {
        this.aadAuthFilterProperties = aadAuthFilterProperties;
        this.serviceEndpointsProperties = serviceEndpointsProperties;
        this.telemetryProxy = new TelemetryProxy(aadAuthFilterProperties.isAllowTelemetry());
    }

    /**
     * Declare AADAuthenticationFilter bean.
     *
     * @return AADAuthenticationFilter bean
     */
    @Bean
    @Scope("singleton")
    @ConditionalOnMissingBean(AADAuthenticationFilter.class)
    public AADAuthenticationFilter azureADJwtTokenFilter() {
        LOG.info("AzureADJwtTokenFilter Constructor.");
        trackCustomEvent();
        return new AADAuthenticationFilter(aadAuthFilterProperties, serviceEndpointsProperties);
    }

    private void trackCustomEvent() {
        telemetryProxy.trackEvent(ClassUtils.getUserClass(this.getClass()).getSimpleName());
    }
}
