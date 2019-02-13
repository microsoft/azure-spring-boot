/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.b2c;

import com.microsoft.azure.telemetry.TelemetryData;
import com.microsoft.azure.telemetry.TelemetryProxy;
import lombok.NonNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;

import java.util.HashMap;
import java.util.Map;

import static com.microsoft.azure.spring.autoconfigure.b2c.AADB2CProperties.*;

@Configuration
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = AADB2CProperties.PREFIX, value = {
        "tenant",
        "client-id",
        LOGOUT_SUCCESS_URL,
        POLICY_SIGN_UP_OR_SIGN_IN_NAME,
        POLICY_SIGN_UP_OR_SIGN_IN_REPLY_URL
})
@EnableConfigurationProperties(AADB2CProperties.class)
public class AADB2CAutoConfiguration {

    private final AADB2CProperties properties;

    public AADB2CAutoConfiguration(@NonNull AADB2CProperties properties) {
        this.properties = properties;

        trackCustomEvent(properties.isAllowTelemetry());
    }

    private void trackCustomEvent(boolean isAllowTelemetry) {
        if (isAllowTelemetry) {
            final TelemetryProxy telemetryProxy = new TelemetryProxy(true);
            final Map<String, String> events = new HashMap<>();

            events.put(TelemetryData.SERVICE_NAME, getClass().getPackage().getName().replaceAll("\\w+\\.", ""));
            events.put(TelemetryData.TENANT_NAME, properties.getTenant());

            telemetryProxy.trackEvent(ClassUtils.getUserClass(getClass()).getSimpleName(), events);
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public AADB2CEntryPoint aadB2CEntryPoint() {
        return new AADB2CEntryPoint(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public AADB2CLogoutSuccessHandler aadB2CLogoutSuccessHandler() {
        return new AADB2CLogoutSuccessHandler(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public AADB2CFilterScenarioHandlerChain aadB2CFilterScenarioChain() {
        return new AADB2CFilterScenarioHandlerChain();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = AADB2CProperties.PREFIX, value = {
            POLICY_PROFILE_EDIT_NAME,
            POLICY_PROFILE_EDIT_REPLY_URL,
            PROFILE_EDIT_URL
    })
    public AADB2CFilterProfileEditHandler profileEditHandler(AADB2CFilterScenarioHandlerChain handlerChain) {
        final AADB2CFilterProfileEditHandler handler = new AADB2CFilterProfileEditHandler(properties);

        handlerChain.addHandler(handler);

        return handler;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = AADB2CProperties.PREFIX, value = {
            POLICY_PASSWORD_RESET_NAME,
            POLICY_PASSWORD_RESET_REPLY_URL,
            PASSWORD_RESET_URL
    })
    public AADB2CFilterPasswordResetHandler passwordResetHandler(AADB2CFilterScenarioHandlerChain handlerChain) {
        final AADB2CFilterPasswordResetHandler handler = new AADB2CFilterPasswordResetHandler(properties);

        handlerChain.addHandler(handler);

        return handler;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(AADB2CFilterPasswordResetHandler.class)
    public AADB2CFilterForgotPasswordHandler forgotPasswordHandler(AADB2CFilterScenarioHandlerChain handlerChain) {
        final AADB2CFilterForgotPasswordHandler handler = new AADB2CFilterForgotPasswordHandler(properties);

        handlerChain.addHandler(handler);

        return handler;
    }

    @Configuration
    public static class OpenIdSessionAutoConfiguration {

        private final AADB2CProperties b2cProperties;

        private final AADB2CFilterScenarioHandlerChain handlerChain;

        public OpenIdSessionAutoConfiguration(@NonNull AADB2CProperties b2cProperties,
                                              @NonNull AADB2CFilterScenarioHandlerChain handlerChain) {
            this.b2cProperties = b2cProperties;
            this.handlerChain = handlerChain;
        }

        @Bean
        @ConditionalOnMissingBean
        public AADB2CFilterPolicyReplyHandler policyReplyHandler() {
            final AADB2CFilterPolicyReplyHandler policyReply = new AADB2CFilterPolicyReplyHandler(b2cProperties);

            handlerChain.addHandler(policyReply);

            return policyReply;
        }

        @Bean
        @ConditionalOnMissingBean
        public AADB2CFilter aadB2CFilter() {
            return new AADB2CFilter(handlerChain);
        }
    }
}
