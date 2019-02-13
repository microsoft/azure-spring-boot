/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package sample.aad.b2c.security;

import com.microsoft.azure.spring.autoconfigure.b2c.AADB2CEntryPoint;
import com.microsoft.azure.spring.autoconfigure.b2c.AADB2CFilter;
import com.microsoft.azure.spring.autoconfigure.b2c.AADB2CLogoutSuccessHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Slf4j
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private AADB2CEntryPoint aadb2CEntryPoint;

    private AADB2CLogoutSuccessHandler aadb2CLogoutSuccessHandler;

    private AADB2CFilter filter;

    public WebSecurityConfiguration(AADB2CEntryPoint entryPoint, AADB2CLogoutSuccessHandler successHandler,
                                    AADB2CFilter filter) {
        this.aadb2CEntryPoint = entryPoint;
        this.aadb2CLogoutSuccessHandler = successHandler;
        this.filter = filter;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/", "/favicon.ico")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic()
                .authenticationEntryPoint(aadb2CEntryPoint)
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessHandler(aadb2CLogoutSuccessHandler).deleteCookies("JSESSIONID")
                .and()
                .csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .and()
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
    }
}

