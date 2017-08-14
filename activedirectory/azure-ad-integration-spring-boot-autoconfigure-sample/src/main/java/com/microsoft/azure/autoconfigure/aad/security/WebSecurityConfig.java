/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.autoconfigure.aad.security;

import com.microsoft.azure.autoconfigure.aad.AzureADJwtTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoRestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.DefaultRequestEnhancer;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.util.MultiValueMap;

@EnableOAuth2Sso
@EnableGlobalMethodSecurity(securedEnabled = true,
        prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public UserInfoRestTemplateCustomizer getUserInfoRestTemplateCustomizer() {
        return new UserInfoRestTemplateCustomizer() {
            //@Override
            public void customize(OAuth2RestTemplate template) {
                template.setAccessTokenProvider(new MyAuthorizationCodeAccessTokenProvider());
            }
        };
    }

    protected class MyAuthorizationCodeAccessTokenProvider extends AuthorizationCodeAccessTokenProvider {
        public MyAuthorizationCodeAccessTokenProvider() {
            setTokenRequestEnhancer(new DefaultRequestEnhancer() {
                @Override
                public void enhance(AccessTokenRequest request,
                                    OAuth2ProtectedResourceDetails resource,
                                    MultiValueMap<String, String> form,
                                    HttpHeaders headers) {
                    super.enhance(request, resource, form, headers);
                    form.set("resource", "https://graph.windows.net/");
                }
            });
        }
    }

    @Autowired
    private AzureADJwtTokenFilter aadJwtFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests().antMatchers("/home").permitAll();
        http.authorizeRequests().antMatchers("/api/**").authenticated();

        http.logout().logoutSuccessUrl("/").permitAll();

        http.authorizeRequests().anyRequest().permitAll();

        http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());

        http.addFilterBefore(aadJwtFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
