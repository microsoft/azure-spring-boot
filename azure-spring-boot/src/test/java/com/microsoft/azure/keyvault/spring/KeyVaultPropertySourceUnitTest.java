/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.keyvault.spring;

import com.microsoft.azure.keyvault.spring.secrets.KeyVaultPropertySource;
import com.microsoft.azure.keyvault.spring.secrets.KeyVaultSecretOperation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static com.microsoft.azure.keyvault.spring.Constants.KEY_VALUE_PREFIX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class KeyVaultPropertySourceUnitTest {
    private static final String testPropertyName1 = "testPropertyName1";
    private static final String testKeyVaultName = "fakeKeyVaultName";

    @Mock
    KeyVaultSecretOperation secretOperation;
    KeyVaultPropertySource keyVaultPropertySource;

    @Before
    public void setup() {
        final List<String> propertyNameList = Arrays.asList(testPropertyName1);

        when(secretOperation.getSecret(anyString(), anyString())).thenReturn(testPropertyName1);
        when(secretOperation.listSecrets(anyString())).thenReturn(propertyNameList);

        keyVaultPropertySource = new KeyVaultPropertySource(secretOperation, testKeyVaultName);
    }

    @Test
    public void testGetPropertyNames() {
        final String[] result = keyVaultPropertySource.getPropertyNames();

        assertThat(result.length).isEqualTo(1);
        assertThat(result[0]).isEqualTo(testPropertyName1);
    }

    @Test
    public void testGetProperty() {
        final String result = (String) keyVaultPropertySource.getProperty(KEY_VALUE_PREFIX + testPropertyName1);
        assertThat(result).isEqualTo(testPropertyName1);
    }
}
