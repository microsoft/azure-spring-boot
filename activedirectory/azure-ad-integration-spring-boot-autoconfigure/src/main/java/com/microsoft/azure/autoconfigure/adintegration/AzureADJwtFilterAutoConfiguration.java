/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.autoconfigure.adintegration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@ConditionalOnMissingBean(AzureADJwtTokenFilter.class)
@EnableConfigurationProperties(AzureADJwtFilterProperties.class)
public class AzureADJwtFilterAutoConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(AzureADJwtFilterProperties.class);

    private final AzureADJwtFilterProperties aadJwtFilterProperties;

    public AzureADJwtFilterAutoConfiguration(AzureADJwtFilterProperties aadJwtFilterProperties) {
        this.aadJwtFilterProperties = aadJwtFilterProperties;
    }

    /**
     * Declare AzureADJwtFilter bean.
     *
     * @return AzureADJwtFilter bean
     */
    @Bean
    @Scope("prototype")
    public AzureADJwtTokenFilter azureADJwtFilter() {
        LOG.info("AzureADJwtTokenFilter Constructor.");
        return new AzureADJwtTokenFilter(aadJwtFilterProperties);
    }

}
