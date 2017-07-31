/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.autoconfigure.msgraph;

import com.microsoft.azure.msgraph.api.Microsoft;
import com.microsoft.azure.msgraph.connect.MicrosoftConnectionFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.social.SocialAutoConfigurerAdapter;
import org.springframework.boot.autoconfigure.social.SocialWebAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.web.GenericConnectionStatusView;

@Configuration
@ConditionalOnClass({SocialConfigurerAdapter.class, MicrosoftConnectionFactory.class})
@ConditionalOnProperty(prefix = "spring.social.microsoft", name = "app-id")
@AutoConfigureBefore(SocialWebAutoConfiguration.class)
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
public class MicrosoftAutoConfiguration {
    @Configuration
    @EnableSocial
    @EnableConfigurationProperties(MicrosoftProperties.class)
    @ConditionalOnWebApplication
    protected static class MicrosoftConfigurerAdapter extends SocialAutoConfigurerAdapter {
        private final MicrosoftProperties properties;

        protected MicrosoftConfigurerAdapter(MicrosoftProperties properties) {
            this.properties = properties;
        }

        @Bean
        @ConditionalOnMissingBean(Microsoft.class)
        @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
        public Microsoft microsoft(ConnectionRepository repository) {
            final Connection<Microsoft> connection = repository
                    .findPrimaryConnection(Microsoft.class);
            return connection != null ? connection.getApi() : null;
        }

        @Bean(name = {"connect/microsoftConnect", "connect/microsoftConnected"})
        @ConditionalOnProperty(prefix = "spring.social", name = "auto-connection-views")
        public GenericConnectionStatusView linkedInConnectView() {
            return new GenericConnectionStatusView("microsoft", "Microsoft");
        }

        @Override
        protected ConnectionFactory<?> createConnectionFactory() {
            return new MicrosoftConnectionFactory(this.properties.getAppId(),
                    this.properties.getAppSecret());
        }
    }
}
