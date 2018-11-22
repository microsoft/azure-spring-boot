/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.aad;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.ResourceRetriever;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import org.junit.Test;
import org.powermock.reflect.Whitebox;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

public class ResourceRetrieverTest {
    private static final int TEST_CONN_TIMEOUT = 1234;
    private static final int TEST_READ_TIMEOUT = 1234;
    private static final int TEST_SIZE_LIMIT = 123400;

    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(AADAuthenticationFilterAutoConfiguration.class))
            .withPropertyValues("azure.activedirectory.client-id=fake-client-id",
                    "azure.activedirectory.client-secret=fake-client-secret",
                    "azure.activedirectory.active-directory-groups=fake-group",
                    "azure.service.endpoints.global.aadKeyDiscoveryUri=http://fake.aad.discovery.uri");

    @Test
    public void resourceRetrieverDefaultConfig() {
        this.contextRunner.run(context -> {
            assertThat(context).hasSingleBean(ResourceRetriever.class);
            final ResourceRetriever retriever = context.getBean(ResourceRetriever.class);
            assertThat(retriever).isInstanceOf(DefaultResourceRetriever.class);

            final DefaultResourceRetriever defaultRetriever = (DefaultResourceRetriever) retriever;
            assertThat(defaultRetriever.getConnectTimeout()).isEqualTo(RemoteJWKSet.DEFAULT_HTTP_CONNECT_TIMEOUT);
            assertThat(defaultRetriever.getReadTimeout()).isEqualTo(RemoteJWKSet.DEFAULT_HTTP_READ_TIMEOUT);
            assertThat(defaultRetriever.getSizeLimit()).isEqualTo(RemoteJWKSet.DEFAULT_HTTP_SIZE_LIMIT);
        });
    }

    @Test
    public void resourceRetriverIsConfigurable() {
        this.contextRunner.withPropertyValues(
                String.format("azure.activedirectory.jwt-connect-timeout=%s", TEST_CONN_TIMEOUT),
                String.format("azure.activedirectory.jwt-read-timeout=%s", TEST_READ_TIMEOUT),
                String.format("azure.activedirectory.jwt-size-limit=%s", TEST_SIZE_LIMIT))
                .run(context -> {
                    assertThat(context).hasSingleBean(ResourceRetriever.class);
                    final ResourceRetriever retriever = context.getBean(ResourceRetriever.class);
                    assertThat(retriever).isInstanceOf(DefaultResourceRetriever.class);

                    final DefaultResourceRetriever defaultRetriever = (DefaultResourceRetriever) retriever;
                    assertThat(defaultRetriever.getConnectTimeout()).isEqualTo(TEST_CONN_TIMEOUT);
                    assertThat(defaultRetriever.getReadTimeout()).isEqualTo(TEST_READ_TIMEOUT);
                    assertThat(defaultRetriever.getSizeLimit()).isEqualTo(TEST_SIZE_LIMIT);
                });
    }

    @Test
    public void validatorUsedConfiguredResourceRetriever() {
        contextRunner.withPropertyValues(
                String.format("azure.activedirectory.jwt-connect-timeout=%s", TEST_CONN_TIMEOUT),
                String.format("azure.activedirectory.jwt-read-timeout=%s", TEST_READ_TIMEOUT),
                String.format("azure.activedirectory.jwt-size-limit=%s", TEST_SIZE_LIMIT)).run(context -> {
            final AADAuthenticationProperties aadAuthProps = context.getBean(AADAuthenticationProperties.class);
            final ServiceEndpointsProperties serviceEndpointsProps = context.getBean(ServiceEndpointsProperties.class);
            final ServiceEndpoints endpoints = serviceEndpointsProps.getServiceEndpoints(
                    aadAuthProps.getEnvironment());
            final ResourceRetriever retriever = context.getBean(ResourceRetriever.class);

            final UserPrincipalManager manager = new UserPrincipalManager(endpoints, retriever);
            final ConfigurableJWTProcessor processor = Whitebox.getInternalState(manager, ConfigurableJWTProcessor.class);
            final JWSKeySelector selector = processor.getJWSKeySelector();
            final JWKSource jwkSource = Whitebox.getInternalState(selector, JWKSource.class);
            assertThat(jwkSource).isInstanceOf(RemoteJWKSet.class);
            final ResourceRetriever validatorRetriever = ((RemoteJWKSet)jwkSource).getResourceRetriever();

            assertThat(validatorRetriever).isInstanceOf(DefaultResourceRetriever.class);
            final DefaultResourceRetriever defaultRetriever = (DefaultResourceRetriever) retriever;
            assertThat(defaultRetriever.getConnectTimeout()).isEqualTo(TEST_CONN_TIMEOUT);
            assertThat(defaultRetriever.getReadTimeout()).isEqualTo(TEST_READ_TIMEOUT);
            assertThat(defaultRetriever.getSizeLimit()).isEqualTo(TEST_SIZE_LIMIT);
        });
    }
}
