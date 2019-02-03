/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package sample.aad.security;

import com.microsoft.azure.spring.autoconfigure.b2c.AADB2CAuthorizationRequestResolver;
import com.microsoft.azure.spring.autoconfigure.b2c.AADB2CLogoutSuccessHandler;
import com.microsoft.azure.spring.autoconfigure.b2c.AADB2CProperties;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private AADB2CProperties properties;

    private AADB2CLogoutSuccessHandler logoutSuccessHandler;

    private AADB2CAuthorizationRequestResolver requestResolver;

    public WebSecurityConfiguration(AADB2CProperties properties,
                                    AADB2CLogoutSuccessHandler logoutSuccessHandler,
                                    AADB2CAuthorizationRequestResolver requestResolver) {
        this.properties = properties;
        this.logoutSuccessHandler = logoutSuccessHandler;
        this.requestResolver = requestResolver;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                // Expire the token from b2c side.
                .logout().logoutSuccessHandler(logoutSuccessHandler)
                .and()
                .oauth2Login()
                // Customize oauth2 processing url from b2c configuration.
                .loginProcessingUrl(properties.getLoginProcessingUrl())
                // Customize oauth2 authorization request.
                .authorizationEndpoint().authorizationRequestResolver(requestResolver);
    }
}
