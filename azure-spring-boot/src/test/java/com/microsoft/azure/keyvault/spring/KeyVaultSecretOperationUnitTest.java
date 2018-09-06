/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.keyvault.spring;

import com.microsoft.azure.Page;
import com.microsoft.azure.PagedList;
import com.microsoft.azure.keyvault.KeyVaultClient;
import com.microsoft.azure.keyvault.models.SecretBundle;
import com.microsoft.azure.keyvault.models.SecretItem;
import com.microsoft.azure.keyvault.spring.secrets.KeyVaultSecretTemplate;
import com.microsoft.rest.RestClient;
import com.microsoft.rest.RestException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberMatcher;
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AbstractKeyVaultTemplate.class)
public class KeyVaultSecretOperationUnitTest {
    private static final String testPropertyName1 = "testPropertyName1";

    @Mock
    KeyVaultClient keyVaultClient;

    KeyVaultSecretTemplate secretTemplate;

    @Before
    public void setup() {

        PagedList<SecretItem> mockResult = new PagedList<SecretItem>() {
            @Override
            public Page<SecretItem> nextPage(String s) throws RestException, IOException {
                final MockPage page = new MockPage();
                return page;
            }
        };

        final SecretItem secretItem = new SecretItem();
        secretItem.withId(testPropertyName1);
        mockResult.add(secretItem);

        final SecretBundle secretBundle = new SecretBundle();
        secretBundle.withValue(testPropertyName1);
        secretBundle.withId(testPropertyName1);

        when(keyVaultClient.listSecrets(anyString())).thenReturn(mockResult);
        when(keyVaultClient.getSecret(anyString(), anyString())).thenReturn(secretBundle);

        secretTemplate = new KeyVaultSecretTemplate("clientId", "clientSecret");
        MemberModifier.stub(MemberMatcher.method(AbstractKeyVaultTemplate.class, "buildKeyVaultClient"))
                .toReturn(keyVaultClient);
    }

    @Test
    public void testGet() {
        final String result = secretTemplate.getSecret("fake", testPropertyName1);

        assertThat(result).isEqualTo(testPropertyName1);
    }

    @Test
    public void testList() {
        final List<String> result = secretTemplate.listSecrets("fake");

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isEqualTo(testPropertyName1);
    }

    class MockPage implements Page<SecretItem> {

        final SecretItem mockSecretItem = new SecretItem();

        @Override
        public String nextPageLink() {
            return null;
        }

        @Override
        public List<SecretItem> items() {
            mockSecretItem.withId("testPropertyName1");
            return Arrays.asList(mockSecretItem);
        }
    }
}
