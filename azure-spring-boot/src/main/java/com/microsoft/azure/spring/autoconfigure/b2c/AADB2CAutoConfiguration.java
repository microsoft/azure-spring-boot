/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.b2c;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.microsoft.azure.spring.autoconfigure.b2c.AADB2CProperties.*;

@Configuration
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = AADB2CProperties.PREFIX, value = {
        "tenant",
        "client-id",
        LOGOUT_SUCCESS_URL,
        POLICY_SIGN_UP_OR_SIGN_IN_NAME,
        POLICY_SIGN_UP_OR_SIGN_IN_REDIRECT_URL
})
@EnableConfigurationProperties(AADB2CProperties.class)
public class AADB2CAutoConfiguration {

    private final AADB2CProperties b2cProperties;

    public AADB2CAutoConfiguration(@NonNull AADB2CProperties b2cProperties) {
        this.b2cProperties = b2cProperties;
    }

    @Bean
    @ConditionalOnMissingBean
    public AADB2CEntryPoint aadB2CEntryPoint() {
        return new AADB2CEntryPoint(b2cProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public AADB2CLogoutSuccessHandler aadB2CLogoutSuccessHandler() {
        return new AADB2CLogoutSuccessHandler(b2cProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public AADB2CFilterPolicyReplyHandler policyReplyHandler() {
        return new AADB2CFilterPolicyReplyHandler(b2cProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public AADB2CFilter aadB2CFilter(AADB2CFilterPolicyReplyHandler policyReply,
                                     @Autowired(required = false) AADB2CFilterPasswordResetHandler passwordReset) {
        return new AADB2CFilter(policyReply, passwordReset);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = AADB2CProperties.PREFIX, value = {
            POLICY_PASSWORD_RESET_NAME,
            POLICY_PASSWORD_RESET_REDIRECT_URL,
            PASSWORD_RESET_URL
    })
    public AADB2CFilterPasswordResetHandler passwordResetHandler() {
        return new AADB2CFilterPasswordResetHandler(b2cProperties);
    }
}
