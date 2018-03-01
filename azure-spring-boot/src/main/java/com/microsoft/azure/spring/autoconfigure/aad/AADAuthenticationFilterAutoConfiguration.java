/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.aad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = "azure.activedirectory", value = {"clientId", "clientSecret"})
@EnableConfigurationProperties({AADAuthenticationFilterProperties.class, ServiceEndpointsProperties.class})
public class AADAuthenticationFilterAutoConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(AADAuthenticationFilterProperties.class);

    private final AADAuthenticationFilterProperties aadAuthFilterProperties;
    private final ServiceEndpointsProperties serviceEndpointsProperties;

    @Autowired
    public AADAuthenticationFilterAutoConfiguration(AADAuthenticationFilterProperties aadAuthFilterProperties,
                                                    ServiceEndpointsProperties serviceEndpointsProperties) {
        this.aadAuthFilterProperties = aadAuthFilterProperties;
        this.serviceEndpointsProperties = serviceEndpointsProperties;
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
        return new AADAuthenticationFilter(aadAuthFilterProperties, serviceEndpointsProperties);
    }
}
