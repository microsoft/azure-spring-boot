/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.keyvault.spring;

import com.azure.core.http.rest.PagedFlux;
import com.azure.core.http.rest.PagedIterable;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;
import com.azure.security.keyvault.secrets.models.SecretProperties;
import com.microsoft.rest.RestException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class KeyVaultOperationUnitTest {
    private static final String secretKeysConfig = "key1,key2,key3";

    private static final String testPropertyName1 = "testPropertyName1";

    private static final String secretKey1 = "key1";

    private static final String fakeVaultUri = "https:fake.vault.com";

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
    private SecretClient keyVaultClient;
    private KeyVaultOperation keyVaultOperation;

    public void setupSecretBundle(String id, String value, String secretKeysConfig) {
        final PagedList<SecretItem> mockResult = new PagedList<SecretItem>() {
            @Override
            public Page<SecretItem> nextPage(String s) throws RestException {
                return new MockPage();
            }
        };
        final KeyVaultSecret secretItem = new KeyVaultSecret(id, value);
        mockResult.add(secretItem);

        final KeyVaultSecret secretBundle = new KeyVaultSecret(id, value);

        when(keyVaultClient.listPropertiesOfSecrets()).thenReturn(mockResult);
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
    }

    @Test
    public void testGetAndMissWhenSecretsProvided() {
        //test get with specific secret key configs
        setupSecretBundle(testPropertyName1, testPropertyName1, secretKeysConfig);
        assertThat(keyVaultOperation.get(testPropertyName1)).isEqualToIgnoringCase(null);
    }

    @Test
    public void testGetAndHitWhenSecretsProvided() {
        setupSecretBundle(secretKey1, secretKey1, secretKeysConfig);
        assertThat(keyVaultOperation.get(secretKey1)).isEqualToIgnoringCase(secretKey1);
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
        //test list with no specific secret keys
        setupSecretBundle(TEST_AZURE_KEYVAULT_NAME, TEST_AZURE_KEYVAULT_NAME, null);

        TEST_SPRING_RELAXED_BINDING_NAMES.forEach(
                n -> assertThat(keyVaultOperation.get(n)).isEqualTo(TEST_AZURE_KEYVAULT_NAME)
        );

        //test list with specific secret key configs
        setupSecretBundle(TEST_AZURE_KEYVAULT_NAME, TEST_AZURE_KEYVAULT_NAME, TEST_AZURE_KEYVAULT_NAME);
        TEST_SPRING_RELAXED_BINDING_NAMES.forEach(
                n -> assertThat(keyVaultOperation.get(n)).isEqualTo(TEST_AZURE_KEYVAULT_NAME)
        );

        setupSecretBundle(TEST_AZURE_KEYVAULT_NAME, TEST_AZURE_KEYVAULT_NAME, secretKeysConfig);
        TEST_SPRING_RELAXED_BINDING_NAMES.forEach(
                n -> assertThat(keyVaultOperation.get(n)).isEqualTo(null)
        );
    }


    class MockPage extends PagedIterable<SecretProperties> {

        final SecretProperties mockSecretItem = new SecretProperties();


        /**
         * Creates instance given {@link PagedFlux}.
         *
         * @param pagedFlux to use as iterable
         */
        public MockPage(PagedFlux<SecretProperties> pagedFlux) {
            super(pagedFlux);
        }
    }
}
