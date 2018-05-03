/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.autoconfigure.mediaservices;

import com.microsoft.azure.telemetry.TelemetryData;
import com.microsoft.azure.telemetry.TelemetryProxy;
import com.microsoft.windowsazure.exception.ServiceException;
import com.microsoft.windowsazure.services.media.MediaConfiguration;
import com.microsoft.windowsazure.services.media.MediaContract;
import com.microsoft.windowsazure.services.media.MediaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;

import java.util.HashMap;

import static com.microsoft.windowsazure.Configuration.*;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Configuration
@ConditionalOnMissingBean(MediaContract.class)
@EnableConfigurationProperties(MediaServicesProperties.class)
@ConditionalOnProperty(prefix = "azure.mediaservices", value = {"account-name", "account-key"})
public class MediaServicesAutoConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(MediaServicesAutoConfiguration.class);

    private final MediaServicesProperties mediaServicesProperties;
    private final TelemetryProxy telemetryProxy;

    public MediaServicesAutoConfiguration(MediaServicesProperties mediaServicesProperties) {
        this.mediaServicesProperties = mediaServicesProperties;
        this.telemetryProxy = new TelemetryProxy(mediaServicesProperties.isAllowTelemetry());
    }

    /**
     * Declare MediaContract bean.
     *
     * @return MediaContract bean
     * @throws ServiceException
     */
    @Bean
    public MediaContract mediaContract() throws ServiceException {
        LOG.debug("mediaContract called");
        trackCustomEvent();
        return createMediaContract();
    }

    private MediaContract createMediaContract() throws ServiceException {
        LOG.debug("createMediaContract called");
        final com.microsoft.windowsazure.Configuration configuration = MediaConfiguration
                .configureWithOAuthAuthentication(
                        MediaServicesProperties.MEDIA_SERVICE_URI,
                        MediaServicesProperties.OAUTH_URI,
                        mediaServicesProperties.getAccountName(),
                        mediaServicesProperties.getAccountKey(),
                        MediaServicesProperties.SCOPE);

        if (nonNull(mediaServicesProperties.getProxyHost())
                && nonNull(mediaServicesProperties.getProxyPort())) {
            configuration.getProperties().put(PROPERTY_HTTP_PROXY_HOST, mediaServicesProperties.getProxyHost());
            configuration.getProperties().put(PROPERTY_HTTP_PROXY_PORT, mediaServicesProperties.getProxyPort());
            configuration.getProperties().put(PROPERTY_HTTP_PROXY_SCHEME, mediaServicesProperties.getProxyScheme());
        } else if (nonNull(mediaServicesProperties.getProxyHost()) && isNull(mediaServicesProperties.getProxyPort())) {
            throw new ServiceException("Please Set Network Proxy port in application.properties");
        } else if (nonNull(mediaServicesProperties.getProxyPort()) && isNull(mediaServicesProperties.getProxyHost())) {
            throw new ServiceException("Please Set Network Proxy host in application.properties");
        }
        return MediaService.create(configuration);
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
