/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.autoconfigure.azuremedia;

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

@Configuration
@ConditionalOnMissingBean(MediaContract.class)
@EnableConfigurationProperties(MediaServicesProperties.class)
public class MediaAutoConfiguration {

    private static final Logger LOG = LoggerFactory
            .getLogger(MediaAutoConfiguration.class);

    private final MediaServicesProperties mediaServicesProperties;

    public MediaAutoConfiguration(MediaServicesProperties mediaServicesProperties) {
        this.mediaServicesProperties = mediaServicesProperties;
    }

    /**
     * Declare MediaContract bean.
     *
     * @return MediaContract bean
     */
    @Bean
    @Scope("prototype")
    public MediaContract cloudStorageAccount() {
        LOG.debug("mediaContract called");
        return createMediaContract();
    }

    private MediaContract createMediaContract() {
        final com.microsoft.windowsazure.Configuration configuration = MediaConfiguration
                .configureWithOAuthAuthentication(
                        mediaServicesProperties.getMediaServiceUri(),
                        mediaServicesProperties.getoAuthUri(),
                        mediaServicesProperties.getClientId(),
                        mediaServicesProperties.getClientSecret(),
                        mediaServicesProperties.getScope());
        return MediaService.create(configuration);
    }
}
