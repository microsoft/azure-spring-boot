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
import com.microsoft.rest.RestException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class KeyVaultOperationUnitTest {
    private static final String secretKeysConfig = "key1,key2,key3";

    private static final String testPropertyName1 = "testPropertyName1";

    private static final String fakeVaultUri = "https://fake.vault.com";

    private static final String TEST_SPRING_RELAXED_BINDING_NAME_0 = "acme.my-project.person.first-name";

    private static final String TEST_SPRING_RELAXED_BINDING_NAME_1 = "acme.myProject.person.firstName";

    private static final String TEST_SPRING_RELAXED_BINDING_NAME_2 = "acme.my_project.person.first_name";

    private static final String TEST_SPRING_RELAXED_BINDING_NAME_3 = "ACME_MYPROJECT_PERSON_FIRSTNAME";

    private static final String TEST_AZURE_KEYVAULT_NAME = "acme-myproject-person-firstname";

    private static final List<String> TEST_SPRING_RELAXED_BINDING_NAMES = Arrays.asList(
            TEST_SPRING_RELAXED_BINDING_NAME_0,
            TEST_SPRING_RELAXED_BINDING_NAME_1,
            TEST_SPRING_RELAXED_BINDING_NAME_2,
            TEST_SPRING_RELAXED_BINDING_NAME_3
    );

    @Mock
    private KeyVaultClient keyVaultClient;
    private KeyVaultOperation keyVaultOperation;

    public void setupSecretBundle(String id, String value, String secretKeysConfig) {
        final PagedList<SecretItem> mockResult = new PagedList<SecretItem>() {
            @Override
            public Page<SecretItem> nextPage(String s) throws RestException {
                return new MockPage();
            }
        };

        final SecretItem secretItem = new SecretItem();
        secretItem.withId(id);
        mockResult.add(secretItem);

        final SecretBundle secretBundle = new SecretBundle();

        secretBundle.withId(id).withValue(value);

        when(keyVaultClient.listSecrets(anyString())).thenReturn(mockResult);
        when(keyVaultClient.getSecret(anyString(), anyString())).thenReturn(secretBundle);
        keyVaultOperation = new KeyVaultOperation(keyVaultClient,
                fakeVaultUri,
                Constants.TOKEN_ACQUIRE_TIMEOUT_SECS,
                secretKeysConfig);
    }

    @Test
    public void testGet() {
        //test get with no specific secret keys
        setupSecretBundle(testPropertyName1, testPropertyName1, null);
        assertThat(keyVaultOperation.get(testPropertyName1)).isEqualToIgnoringCase(testPropertyName1);

        //test get with specific secret key configs
        setupSecretBundle(testPropertyName1, testPropertyName1, secretKeysConfig);
        assertThat(keyVaultOperation.get(testPropertyName1)).isEqualToIgnoringCase(null);
    }

    @Test
    public void testList() {
        //test list with no specific secret keys
        setupSecretBundle(testPropertyName1, testPropertyName1, null);
        final String[] result = keyVaultOperation.list();
        assertThat(result.length).isEqualTo(1);
        assertThat(result[0]).isEqualToIgnoringCase(testPropertyName1);

        //test list with specific secret key configs
        setupSecretBundle(testPropertyName1, testPropertyName1, secretKeysConfig);
        final String[] specificResult = keyVaultOperation.list();
        assertThat(specificResult.length).isEqualTo(3);
        assertThat(specificResult[0]).isEqualTo(secretKeysConfig.split(",")[0]);
    }

    @Test
    public void setTestSpringRelaxedBindingNames() {
        setupSecretBundle(TEST_AZURE_KEYVAULT_NAME, TEST_AZURE_KEYVAULT_NAME, null);

        TEST_SPRING_RELAXED_BINDING_NAMES.forEach(
                n -> assertThat(keyVaultOperation.get(n)).isEqualTo(TEST_AZURE_KEYVAULT_NAME)
        );
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
            return Collections.singletonList(mockSecretItem);
        }
    }
}
