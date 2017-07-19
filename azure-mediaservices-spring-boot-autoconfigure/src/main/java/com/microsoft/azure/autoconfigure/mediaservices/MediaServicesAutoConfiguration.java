/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.autoconfigure.mediaservices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.microsoft.windowsazure.services.media.MediaConfiguration;
import com.microsoft.windowsazure.services.media.MediaContract;
import com.microsoft.windowsazure.services.media.MediaService;

/**
 * @author rajakolli
 *
 */
@Configuration
@ConditionalOnMissingBean(MediaContract.class)
@EnableConfigurationProperties(MediaServicesProperties.class)
public class MediaServicesAutoConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(MediaServicesAutoConfiguration.class);

    private final MediaServicesProperties mediaServicesProperties;

    public MediaServicesAutoConfiguration(MediaServicesProperties mediaServicesProperties) {
        this.mediaServicesProperties = mediaServicesProperties;
    }

    /**
     * Declare MediaContract bean.
     *
     * @return MediaContract bean
     */
    @Bean
    @Scope("prototype")
    public MediaContract mediaContract() {
        LOG.debug("mediaContract called");
        return createMediaContract();
    }

    private MediaContract createMediaContract() {
        LOG.debug("createMediaContract called");
        final com.microsoft.windowsazure.Configuration configuration = MediaConfiguration
                .configureWithOAuthAuthentication(
                        mediaServicesProperties.getMediaServiceUri(),
                        mediaServicesProperties.getoAuthUri(),
                        mediaServicesProperties.getAccountName(),
                        mediaServicesProperties.getAccountKey(),
                        mediaServicesProperties.getScope());
        return MediaService.create(configuration);
    }
}
