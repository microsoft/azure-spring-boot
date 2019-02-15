/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package sample.aad.security;

import com.microsoft.azure.spring.autoconfigure.b2c.AADB2CAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private AADB2CAuthenticationProvider provider;

    public WebSecurityConfiguration(AADB2CAuthenticationProvider provider) {
        this.provider = provider;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                // Expire the token from b2c side.
                .logout().logoutSuccessHandler(provider.getLogoutSuccessHandler())
                .and()
                .oauth2Login()
                // Customize oauth2 processing url from b2c configuration.
                .loginProcessingUrl(provider.getLoginProcessingUrl())
                // Customize oauth2 authorization request.
                .authorizationEndpoint().authorizationRequestResolver(provider.getAuthorizationRequestResolver());
    }
}
