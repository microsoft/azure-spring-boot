/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.aad;

import com.microsoft.aad.adal4j.ClientCredential;
import com.microsoft.azure.telemetry.TelemetryData;
import com.microsoft.azure.telemetry.TelemetryProxy;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.ResourceRetriever;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.util.ClassUtils;

import java.util.HashMap;

@Configuration
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = "azure.activedirectory", value = {"client-id", "client-secret"})
@EnableConfigurationProperties({AADAuthenticationProperties.class, ServiceEndpointsProperties.class})
@PropertySource(value = "classpath:serviceEndpoints.properties")
@Import(AADGraphHttpClientConfiguration.class)
public class AADAuthenticationFilterAutoConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(AADAuthenticationProperties.class);

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
     * @param resourceResolve    - {@link ResourceRetriever} to handle getting the JWT.
     * @param azureADGraphClient - Used to interact with the Graph API.
     * @return AADAuthenticationFilter bean
     */
    @Bean
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    @ConditionalOnMissingBean(AADAuthenticationFilter.class)
    public AADAuthenticationFilter azureADJwtTokenFilter(ResourceRetriever resourceResolve,
                                                         AzureADGraphClient azureADGraphClient) {
        LOG.info("AzureADJwtTokenFilter Constructor.");
        trackCustomEvent();
        return new AADAuthenticationFilter(aadAuthProps, serviceEndpointsProps,
                resourceResolve, azureADGraphClient);
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    @ConditionalOnMissingBean(ResourceRetriever.class)
    public ResourceRetriever getJWTResourceRetriever(AADAuthenticationProperties aadAuthProps) {
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
