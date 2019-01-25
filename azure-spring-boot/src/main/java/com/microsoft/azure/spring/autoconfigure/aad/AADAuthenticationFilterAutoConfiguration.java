/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.aad;

import com.microsoft.azure.telemetry.TelemetryData;
import com.microsoft.azure.telemetry.TelemetryProxy;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.ResourceRetriever;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.ClassUtils;

@Configuration
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = "azure.activedirectory", value = {"client-id"})
@EnableConfigurationProperties({AADAuthenticationProperties.class, ServiceEndpointsProperties.class})
@PropertySource(value = "classpath:serviceEndpoints.properties")
public class AADAuthenticationFilterAutoConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(AADAuthenticationProperties.class);

    private static final String PROPERTY_PREFIX = "azure.activedirectory";
    private static final String PROPERTY_SESSION_STATELESS = "session-stateless";

    private final AADAuthenticationProperties aadAuthProps;
    private final ServiceEndpointsProperties serviceEndpointsProps;

    private final TelemetryProxy telemetryProxy;

    public AADAuthenticationFilterAutoConfiguration(AADAuthenticationProperties aadAuthFilterProps,
                                                    ServiceEndpointsProperties serviceEndpointsProps) {
        this.aadAuthProps = aadAuthFilterProps;
        this.serviceEndpointsProps = serviceEndpointsProps;
        this.telemetryProxy = new TelemetryProxy(aadAuthFilterProps.isAllowTelemetry());
    }

    /**
     * Declare AADAuthenticationFilter bean.
     *
     * @return AADAuthenticationFilter bean
     */
    @Bean
    @ConditionalOnMissingBean(AADAuthenticationFilter.class)
    @ConditionalOnProperty(prefix = PROPERTY_PREFIX, value = {"client-id", "client-secret"})
    @ConditionalOnExpression("${azure.activedirectory.session-stateless:false} == false")
    public AADAuthenticationFilter azureADJwtTokenFilter() {
        LOG.info("AzureADJwtTokenFilter Constructor.");
        trackCustomEvent();
        return new AADAuthenticationFilter(aadAuthProps, serviceEndpointsProps, getJWTResourceRetriever());
    }

    @Bean
    @ConditionalOnMissingBean(AADAppRoleAuthenticationFilter.class)
    @ConditionalOnProperty(prefix = PROPERTY_PREFIX, value = PROPERTY_SESSION_STATELESS, havingValue = "true")
    public AADAppRoleAuthenticationFilter azureADStatelessAuthFilter(ResourceRetriever resourceRetriever) {
        LOG.info("Creating AzureADStatelessAuthFilter bean.");
        return new AADAppRoleAuthenticationFilter(new UserPrincipalManager(serviceEndpointsProps, aadAuthProps,
            resourceRetriever));
    }

    @Bean
    @ConditionalOnMissingBean(ResourceRetriever.class)
    public ResourceRetriever getJWTResourceRetriever() {
        return new DefaultResourceRetriever(aadAuthProps.getJwtConnectTimeout(), aadAuthProps.getJwtReadTimeout(),
                aadAuthProps.getJwtSizeLimit());
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
