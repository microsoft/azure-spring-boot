/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.keyvault.spring;

import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;
import static com.microsoft.azure.keyvault.spring.Constants.DEFAULT_REFRESH_INTERVAL_MS;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class CaseSensitiveKeyVaultTest {
    @Mock
    private SecretClient keyVaultClient;

    @Test
    public void testGet() {
        final KeyVaultOperation keyVaultOperation = new KeyVaultOperation(
                keyVaultClient,
                DEFAULT_REFRESH_INTERVAL_MS,
                true);

        final KeyVaultSecret key1 = new KeyVaultSecret("key1", "value1");
        when(keyVaultClient.getSecret("key1")).thenReturn(key1);
        final KeyVaultSecret key2 = new KeyVaultSecret("Key2", "Value2");
        when(keyVaultClient.getSecret("Key2")).thenReturn(key2);
        
        assertEquals("value1", keyVaultOperation.getProperty("key1"));
        assertEquals("Value2", keyVaultOperation.getProperty("Key2"));
    }
}
