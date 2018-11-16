/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.keyvault.spring;

import com.microsoft.azure.credentials.AppServiceMSICredentials;
import com.microsoft.azure.credentials.MSICredentials;
import com.microsoft.rest.credentials.ServiceClientCredentials;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.mock.env.MockEnvironment;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;

public class KeyVaultEnvironmentPostProcessorHelperUnitTest {

    private KeyVaultEnvironmentPostProcessorHelper keyVaultEnvironmentPostProcessorHelper;
    private ConfigurableEnvironment environment;
    private MutablePropertySources propertySources;
    private Map<String, Object> msiProperties = new HashMap<>();

    @Before
    public void setup() {
        environment = new MockEnvironment();
        propertySources = environment.getPropertySources();
    }

    @Test
    public void testGetCredentialsWhenMSIEnabledInAppService() {
        msiProperties.put("MSI_ENDPOINT", "fakeendpoint");
        msiProperties.put("MSI_SECRET", "fakesecret");
        propertySources.addLast(new MapPropertySource("MSI_Properties", msiProperties));

        keyVaultEnvironmentPostProcessorHelper = new KeyVaultEnvironmentPostProcessorHelper(environment);

        final ServiceClientCredentials credentials = keyVaultEnvironmentPostProcessorHelper.getCredentials();

        assertThat(credentials, IsInstanceOf.instanceOf(AppServiceMSICredentials.class));
    }

    @Test
    public void testGetCredentialsWhenUsingClientAndKey() {
        msiProperties.put("azure.keyvault.client-id", "aaaa-bbbb-cccc-dddd");
        msiProperties.put("azure.keyvault.client-key", "mySecret");
        propertySources.addLast(new MapPropertySource("MSI_Properties", msiProperties));

        keyVaultEnvironmentPostProcessorHelper = new KeyVaultEnvironmentPostProcessorHelper(environment);

        final ServiceClientCredentials credentials = keyVaultEnvironmentPostProcessorHelper.getCredentials();

        assertThat(credentials, IsInstanceOf.instanceOf(AzureKeyVaultCredential.class));
    }

    @Test
    public void testGetCredentialsWhenMSIEnabledInVMWithClientId() {
        msiProperties.put("azure.keyvault.client-id", "aaaa-bbbb-cccc-dddd");
        propertySources.addLast(new MapPropertySource("MSI_Properties", msiProperties));

        keyVaultEnvironmentPostProcessorHelper = new KeyVaultEnvironmentPostProcessorHelper(environment);

        final ServiceClientCredentials credentials = keyVaultEnvironmentPostProcessorHelper.getCredentials();

        assertThat(credentials, IsInstanceOf.instanceOf(MSICredentials.class));
    }

    @Test
    public void testGetCredentialsWhenMSIEnabledInVMWithoutClientId() {
        keyVaultEnvironmentPostProcessorHelper = new KeyVaultEnvironmentPostProcessorHelper(environment);

        final ServiceClientCredentials credentials = keyVaultEnvironmentPostProcessorHelper.getCredentials();

        assertThat(credentials, IsInstanceOf.instanceOf(MSICredentials.class));
    }
}
