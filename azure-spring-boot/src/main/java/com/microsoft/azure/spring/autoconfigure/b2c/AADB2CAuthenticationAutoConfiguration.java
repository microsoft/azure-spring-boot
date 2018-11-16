/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.b2c;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static com.microsoft.azure.spring.autoconfigure.b2c.AADB2CAuthenticationProperties.POLICY_SIGN_UP_OR_SIGN_IN_NAME;
import static com.microsoft.azure.spring.autoconfigure.b2c.AADB2CAuthenticationProperties.POLICY_SIGN_UP_OR_SIGN_IN_REDIRECT_URL;

@Configuration
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = AADB2CAuthenticationProperties.PREFIX, value = {
        "tenant",
        "client-id",
        POLICY_SIGN_UP_OR_SIGN_IN_NAME,
        POLICY_SIGN_UP_OR_SIGN_IN_REDIRECT_URL,
})
@EnableConfigurationProperties(AADB2CAuthenticationProperties.class)
public class AADB2CAuthenticationAutoConfiguration {

    @Getter(AccessLevel.PROTECTED) // TODO(pan): make the findbugs happy, will remove this after referenced.
    private final AADB2CAuthenticationProperties b2cProperties;

    public AADB2CAuthenticationAutoConfiguration(@NonNull AADB2CAuthenticationProperties b2cProperties) {
        this.b2cProperties = b2cProperties;
    }
}
