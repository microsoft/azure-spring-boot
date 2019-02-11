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
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.microsoft.azure.spring.autoconfigure.b2c.AADB2CProperties.*;

@Configuration
@ConditionalOnWebApplication
@ConditionalOnProperty(
        prefix = PREFIX,
        value = {
                "tenant",
                "client-id",
                "client-secret",
                "reply-url",
                POLICY_SIGN_UP_OR_SIGN_IN
        }
)
@EnableConfigurationProperties(AADB2CProperties.class)
public class AADB2CAutoConfiguration {

    private final ClientRegistrationRepository repository;

    private final AADB2CProperties properties;

    public AADB2CAutoConfiguration(@NonNull ClientRegistrationRepository repository,
                                   @NonNull AADB2CProperties properties) {
        this.repository = repository;
        this.properties = properties;
    }

    @Bean
    @ConditionalOnMissingBean
    public AADB2CAuthorizationRequestResolver b2cOAuth2AuthorizationRequestResolver() {
        return new AADB2CAuthorizationRequestResolver(repository);
    }

    @Bean
    @ConditionalOnMissingBean
    public AADB2CLogoutSuccessHandler b2cLogoutSuccessHandler() {
        return new AADB2CLogoutSuccessHandler(properties);
    }

    @Configuration
    @ConditionalOnProperty(prefix = PREFIX, value = "oidc-enabled", havingValue = "true", matchIfMissing = true)
    public static class AADB2COidcAutoConfiguration {

        private final AADB2CProperties properties;

        public AADB2COidcAutoConfiguration(@NonNull AADB2CProperties properties) {
            this.properties = properties;
        }

        private void addB2CClientRegistration(@NonNull List<ClientRegistration> registrations, String policeValue) {
            if (StringUtils.hasText(policeValue)) {
                registrations.add(b2cClientRegistration(policeValue));
            }
        }

        @Bean
        @ConditionalOnMissingBean
        public ClientRegistrationRepository clientRegistrationRepository() {
            final List<ClientRegistration> registrations = new ArrayList<>();

            addB2CClientRegistration(registrations, properties.getPolicies().getSignUpOrSignIn());
            addB2CClientRegistration(registrations, properties.getPolicies().getProfileEdit());
            addB2CClientRegistration(registrations, properties.getPolicies().getPasswordReset());

            return new InMemoryClientRegistrationRepository(registrations);
        }

        private ClientRegistration b2cClientRegistration(String policyValue) {
            Assert.hasText(policyValue, "value should contains text.");

            return ClientRegistration.withRegistrationId(policyValue) // Use policy value as registration Id.
                    .clientId(properties.getClientId())
                    .clientSecret(properties.getClientSecret())
                    .clientAuthenticationMethod(ClientAuthenticationMethod.POST)
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .redirectUriTemplate(properties.getReplyUrl())
                    .scope(properties.getClientId(), "openid")
                    .authorizationUri(AADB2CURL.getAuthorizationUrl(properties.getTenant()))
                    .tokenUri(AADB2CURL.getTokenUrl(properties.getTenant(), policyValue))
                    .jwkSetUri(AADB2CURL.getJwkSetUrl(properties.getTenant(), policyValue))
                    .userNameAttributeName("name")
                    .clientName(policyValue)
                    .build();
        }
    }
}
