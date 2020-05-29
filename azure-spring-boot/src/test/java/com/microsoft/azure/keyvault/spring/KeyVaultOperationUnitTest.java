/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.keyvault.spring;

import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class KeyVaultOperationUnitTest {
    private static final List<String> secretKeysConfig = Arrays.asList("key1", "key2", "key3");

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

    public void setupSecretBundle(String id, String value, List<String> secretKeysConfig) {
        keyVaultOperation = new KeyVaultOperation(
                keyVaultClient,
                0,
                secretKeysConfig,
                false);
    }

    @Test
    public void testGetWithNoSpecficSecretKeys() {
        setupSecretBundle(testPropertyName1, testPropertyName1, null);
        
        final LinkedHashMap<String, String> properties = new LinkedHashMap<>();
        properties.put("testpropertyname1", testPropertyName1);
        keyVaultOperation.setProperties(properties);
        
        assertThat(keyVaultOperation.getProperty(testPropertyName1)).isEqualToIgnoringCase(testPropertyName1);
    }

    @Test
    public void testGetAndMissWhenSecretsProvided() {
        setupSecretBundle(testPropertyName1, testPropertyName1, secretKeysConfig);
        
        final LinkedHashMap<String, String> properties = new LinkedHashMap<>();
        properties.put("key1", "value1");
        properties.put("key2", "value2");
        properties.put("key3", "value3");
        keyVaultOperation.setProperties(properties);
        
        assertThat(keyVaultOperation.getProperty(testPropertyName1)).isEqualToIgnoringCase(null);
    }

    @Test
    public void testGetAndHitWhenSecretsProvided() {
        when(keyVaultClient.getSecret("key1")).thenReturn(new KeyVaultSecret("key1", "key1"));
        when(keyVaultClient.getSecret("key2")).thenReturn(new KeyVaultSecret("key2", "key2"));
        when(keyVaultClient.getSecret("key3")).thenReturn(new KeyVaultSecret("key3", "key3"));
        
        setupSecretBundle(secretKey1, secretKey1, secretKeysConfig);
        
        assertThat(keyVaultOperation.getProperty(secretKey1)).isEqualToIgnoringCase(secretKey1);
    }

    @Test
    public void testList() {
        //test list with no specific secret keys
        setupSecretBundle(testPropertyName1, testPropertyName1, null);
        final LinkedHashMap<String, String> properties = new LinkedHashMap<>();
        properties.put(testPropertyName1, testPropertyName1);
        keyVaultOperation.setProperties(properties);
        final String[] result = keyVaultOperation.getPropertyNames();
        assertThat(result.length).isEqualTo(1);
        assertThat(result[0]).isEqualToIgnoringCase(testPropertyName1);

        //test list with specific secret key configs
        when(keyVaultClient.getSecret("key1")).thenReturn(new KeyVaultSecret("key1", "key1"));
        when(keyVaultClient.getSecret("key2")).thenReturn(new KeyVaultSecret("key2", "key2"));
        when(keyVaultClient.getSecret("key3")).thenReturn(new KeyVaultSecret("key3", "key3"));
        setupSecretBundle(testPropertyName1, testPropertyName1, secretKeysConfig);
        final String[] specificResult = keyVaultOperation.getPropertyNames();
        assertThat(specificResult.length).isEqualTo(3);
        assertThat(specificResult[0]).isEqualTo(secretKeysConfig.get(0));
    }

    @Test
    public void setTestSpringRelaxedBindingNames() {
        //test list with no specific secret keys
        setupSecretBundle(TEST_AZURE_KEYVAULT_NAME, TEST_AZURE_KEYVAULT_NAME, null);
        LinkedHashMap<String, String> properties = new LinkedHashMap<>();
        properties.put("acme-myproject-person-firstname", TEST_AZURE_KEYVAULT_NAME);
        keyVaultOperation.setProperties(properties);
        TEST_SPRING_RELAXED_BINDING_NAMES.forEach(
                n -> assertThat(keyVaultOperation.getProperty(n)).isEqualTo(TEST_AZURE_KEYVAULT_NAME)
        );

        //test list with specific secret key configs
        setupSecretBundle(TEST_AZURE_KEYVAULT_NAME, TEST_AZURE_KEYVAULT_NAME, Arrays.asList(TEST_AZURE_KEYVAULT_NAME));
        properties = new LinkedHashMap<>();
        properties.put(TEST_AZURE_KEYVAULT_NAME, TEST_AZURE_KEYVAULT_NAME);
        keyVaultOperation.setProperties(properties);
        TEST_SPRING_RELAXED_BINDING_NAMES.forEach(
                n -> assertThat(keyVaultOperation.getProperty(n)).isEqualTo(TEST_AZURE_KEYVAULT_NAME)
        );

        setupSecretBundle(TEST_AZURE_KEYVAULT_NAME, TEST_AZURE_KEYVAULT_NAME, secretKeysConfig);
        properties = new LinkedHashMap<>();
        properties.put("key1", "key1");
        properties.put("key2", "key2");
        properties.put("key3", "key3");
        keyVaultOperation.setProperties(properties);
        TEST_SPRING_RELAXED_BINDING_NAMES.forEach(
                n -> assertThat(keyVaultOperation.getProperty(n)).isEqualTo(null)
        );
    }
}
