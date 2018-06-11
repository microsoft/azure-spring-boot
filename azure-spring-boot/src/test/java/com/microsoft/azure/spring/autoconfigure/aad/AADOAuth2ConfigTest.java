/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.aad;

import io.jsonwebtoken.lang.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

public class AADOAuth2ConfigTest {
    private static final String AAD_OAUTH2_MINIMUM_PROPS = "aad-backend-oauth2-minimum.properties";
    private Resource testResource;
    private ResourcePropertySource testPropResource;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private AnnotationConfigWebApplicationContext testContext;

    @Before
    public void setup() throws Exception {
        testResource = new ClassPathResource(AAD_OAUTH2_MINIMUM_PROPS);
        testPropResource = new ResourcePropertySource("test", testResource);
    }

    @After
    public void clear() {
        if (testContext != null) {
            testContext.close();
        }
    }

    @Test
    public void noOAuth2UserServiceBeanCreatedIfPropsNotConfigured() {
        final AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(AADOAuth2AutoConfiguration.class);
        context.refresh();

        exception.expect(NoSuchBeanDefinitionException.class);
        context.getBean(OAuth2UserService.class);
    }

    @Test
    public void testOAuth2UserServiceBeanCreatedIfPropsConfigured() {
        testContext = initTestContext();
        Assert.notNull(testContext.getBean(OAuth2UserService.class));
    }

    @Test
    public void noOAuth2UserServiceBeanCreatedIfTenantIdNotConfigured() {
        testPropResource.getSource().remove(Constants.TENANT_ID_PROPERTY);
        testContext = initTestContext();

        exception.expect(NoSuchBeanDefinitionException.class);
        testContext.getBean(OAuth2UserService.class);
    }

    private AnnotationConfigWebApplicationContext initTestContext() {
        final AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();

        context.getEnvironment().getPropertySources().addLast(testPropResource);
        context.register(AADOAuth2AutoConfiguration.class);
        context.refresh();

        return context;
    }
}
