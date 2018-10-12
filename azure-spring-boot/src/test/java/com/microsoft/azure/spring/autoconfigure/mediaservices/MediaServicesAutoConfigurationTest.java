/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.autoconfigure.mediaservices;

import com.microsoft.windowsazure.services.media.MediaContract;
import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static com.microsoft.azure.spring.autoconfigure.mediaservices.Constants.*;
import static com.microsoft.azure.utils.TestUtils.propPair;
import static org.assertj.core.api.Assertions.assertThat;

public class MediaServicesAutoConfigurationTest {
    private ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(MediaServicesAutoConfiguration.class));

    @Test
    public void mediaContractBeanCanBeCreated() {
        contextRunner.withPropertyValues(propPair(TENANT_PROP, TENANT),
                propPair(CLIENT_ID_PROP, CLIENT_ID),
                propPair(CLIENT_SECRET_PROP, CLIENT_SECRET),
                propPair(REST_API_ENDPOINT_PROP, REST_API_ENDPOINT))
                .run(context ->
                        assertThat(context).hasSingleBean(MediaContract.class)
                );
    }

    @Test(expected = NoSuchBeanDefinitionException.class)
    public void byDefaultMediaContractBeanNotCreated() {
        contextRunner.run(context -> context.getBean(MediaContract.class));
    }
}
