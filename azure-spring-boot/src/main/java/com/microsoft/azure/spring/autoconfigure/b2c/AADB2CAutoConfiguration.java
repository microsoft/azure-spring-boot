/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.b2c;

import lombok.NonNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

import java.util.UUID;

import static com.microsoft.azure.spring.autoconfigure.b2c.AADB2CProperties.*;

@Configuration
@ConditionalOnWebApplication
@ConditionalOnProperty(
        prefix = PREFIX,
        value = {
                "tenant",
                "client-id",
                "client-secret",
                POLICY_SIGN_UP_OR_SIGN_IN_NAME,
                POLICY_SIGN_UP_OR_SIGN_IN_REPLY_URL
        }
)
@EnableConfigurationProperties(AADB2CProperties.class)
public class AADB2CAutoConfiguration {

    private static final String CLIENT_NAME = "aad-b2c";

    private static final String REGISTRATION_ID = "aad-b2c-" + UUID.randomUUID();

    private final ClientRegistrationRepository repository;

    private final AADB2CProperties properties;

    public AADB2CAutoConfiguration(@NonNull ClientRegistrationRepository repository,
                                   @NonNull AADB2CProperties properties) {
        this.repository = repository;
        this.properties = properties;
    }

    @Bean
    @ConditionalOnMissingBean
    public OAuth2AuthorizationRequestResolver b2cOAuth2AuthorizationRequestResolver() {
        return new AADB2CAuthorizationRequestResolver(repository, properties);
    }

    @Configuration
    @ConditionalOnProperty(prefix = PREFIX, value = "oidc-enabled", havingValue = "true", matchIfMissing = true)
    public static class AADB2COidcAutoConfiguration {

        private final AADB2CProperties properties;

        public AADB2COidcAutoConfiguration(@NonNull AADB2CProperties properties) {
            this.properties = properties;
        }

        @Bean
        @ConditionalOnMissingBean
        public ClientRegistrationRepository clientRegistrationRepository() {
            return new InMemoryClientRegistrationRepository(this.b2cOIDCClientRegistration());
        }

        private ClientRegistration b2cOIDCClientRegistration() {
            final AADB2CProperties.Policy policy = properties.getPolicies().getSignUpOrSignIn();

            return ClientRegistration.withRegistrationId(REGISTRATION_ID)
                    .clientId(properties.getClientId())
                    .clientSecret(properties.getClientSecret())
                    .clientAuthenticationMethod(ClientAuthenticationMethod.POST)
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .redirectUriTemplate(policy.getReplyURL())
                    .scope(properties.getClientId(), "openid")
                    .authorizationUri(AADB2CURL.getAuthorizationUri(properties.getTenant()))
                    .tokenUri(AADB2CURL.getTokenUri(properties.getTenant(), policy.getName()))
                    .jwkSetUri(AADB2CURL.getJwkSetUri(properties.getTenant(), policy.getName()))
                    .userNameAttributeName("name")
                    .clientName(CLIENT_NAME)
                    .build();
        }
    }
}
