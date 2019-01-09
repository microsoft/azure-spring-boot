/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.aad;

import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.ResourceRetriever;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = "azure.aad.app-role", value = {"client-id"})
@EnableConfigurationProperties({AADAppRoleAuthenticationProperties.class,
    ServiceEndpointsProperties.class})
@PropertySource(value = "classpath:serviceEndpoints.properties")
public class AADAppRoleAuthenticationFilterAutoConfiguration {

    private final AADAppRoleAuthenticationProperties authenticationProperties;

    public AADAppRoleAuthenticationFilterAutoConfiguration(
        AADAppRoleAuthenticationProperties authenticationProperties) {
        this.authenticationProperties = authenticationProperties;
    }

    @Bean
    @ConditionalOnMissingBean(UserPrincipalManager.class)
    public UserPrincipalManager userPrincipalManager(
        ServiceEndpointsProperties serviceEndpointsProps, AADAppRoleAuthenticationProperties aadAuthProps,
        ResourceRetriever resourceRetriever) {
        return new UserPrincipalManager(serviceEndpointsProps, aadAuthProps, resourceRetriever);
    }

    @Bean
    @ConditionalOnMissingBean(AADAppRoleAuthenticationFilter.class)
    public AADAppRoleAuthenticationFilter authFilter(UserPrincipalManager userPrincipalManager) {
        return new AADAppRoleAuthenticationFilter(userPrincipalManager);
    }

    @Bean
    @ConditionalOnMissingBean(ResourceRetriever.class)
    public ResourceRetriever getJWTResourceRetriever() {
        return new DefaultResourceRetriever(authenticationProperties.getJwtConnectTimeout(),
            authenticationProperties.getJwtReadTimeout(),
            authenticationProperties.getJwtSizeLimit());
    }
}
