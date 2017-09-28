/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.keyvault.spring;

import org.junit.Before;
import org.junit.Test;

public class AzureKeyVaultCredentialUnitTest {

    private AzureKeyVaultCredential keyVaultCredential;

    @Before
    public void setup() {
        keyVaultCredential = new AzureKeyVaultCredential("fakeClientId", "fakeClientKey", 30);
    }

    @Test(expected = RuntimeException.class)
    public void testDoAuthenticationRejctIfInvalidCredential() {
        keyVaultCredential.doAuthenticate("https://fakeauthorizationurl.com", "keyvault", "scope");
    }
}
