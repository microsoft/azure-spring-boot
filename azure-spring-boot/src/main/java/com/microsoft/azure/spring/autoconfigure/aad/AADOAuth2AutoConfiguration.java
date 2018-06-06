/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.aad;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import javax.naming.ServiceUnavailableException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Configuration
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = "azure.activedirectory", value = {"client-id", "client-secret", "tenant-id"})
@EnableConfigurationProperties({AADAuthenticationProperties.class, ServiceEndpointsProperties.class})
public class AADOAuth2AutoConfiguration {
    private AADAuthenticationProperties aadAuthProps;
    private ServiceEndpointsProperties serviceEndpointsProps;

    public AADOAuth2AutoConfiguration(AADAuthenticationProperties aadAuthProperties,
                                      ServiceEndpointsProperties serviceEndpointsProps) {
        this.aadAuthProps = aadAuthProperties;
        this.serviceEndpointsProps = serviceEndpointsProps;
    }

    @Bean
    @ConditionalOnProperty(prefix = "azure.activedirectory", value = {"ActiveDirectoryGroups"})
    public OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        return this.delegationOidcUserService();
    }

    private OAuth2UserService<OidcUserRequest, OidcUser> delegationOidcUserService() {
        final OidcUserService delegate = new OidcUserService();

        return (userRequest) -> {
            // Delegate to the default implementation for loading a user
            OidcUser oidcUser = delegate.loadUser(userRequest);
            final OidcIdToken idToken = userRequest.getIdToken();

            final String graphApiToken;
            final Set<GrantedAuthority> mappedAuthorities;

            try {
                // https://github.com/MicrosoftDocs/azure-docs/issues/8121#issuecomment-387090099
                // In AAD App Registration configure oauth2AllowImplicitFlow to true
                final AzureADGraphClient graphClient = new AzureADGraphClient(aadAuthProps, serviceEndpointsProps);
                graphApiToken = graphClient.acquireTokenForGraphApi(idToken.getTokenValue().toString(),
                        aadAuthProps.getTenantId()).getAccessToken();

                mappedAuthorities = graphClient.getGrantedAuthorities(graphApiToken);
            } catch (MalformedURLException | ServiceUnavailableException
                    | InterruptedException | ExecutionException e) {
                throw new IllegalStateException("Failed to acquire token for Graph API.", e);
            } catch (IOException ioe) {
                throw new IllegalStateException("Failed to map group to authorities.", ioe);
            }

            // Create a copy of oidcUser but use the mappedAuthorities instead
            oidcUser = new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo());

            return oidcUser;
        };
    }
}
