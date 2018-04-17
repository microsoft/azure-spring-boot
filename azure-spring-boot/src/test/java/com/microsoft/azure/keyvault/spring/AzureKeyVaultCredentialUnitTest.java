/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.keyvault.spring;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

public class AzureKeyVaultCredentialUnitTest {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();
    private AzureKeyVaultCredential keyVaultCredential;
    private KeyVaultCertificateCredentials keyVaultCredentialViaCertificate;
    private KeyVaultMsiCredentials keyVaultMsiCredentials;


    @Before
    public void setup() {
        keyVaultCredential = new AzureKeyVaultCredential("fakeClientId", "fakeClientKey", 30);
    }

    @Test(expected = RuntimeException.class)
    public void testDoAuthenticationRejectIfInvalidCredential() {
        keyVaultCredential.doAuthenticate("https://fakeauthorizationurl.com", "keyvault", "scope");
    }

    @Test
    public void testDoAuthenticationRejectIfInvalidCertificateCredentialWithoutPathAndPassword(){
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("KeyVault Authentication by certificate failed:  (No such file or directory)");
        keyVaultCredentialViaCertificate = new KeyVaultCertificateCredentials(
                "fakeClientId", "", "", 30);
        keyVaultCredentialViaCertificate.doAuthenticate("https://fakeauthorizationurl.com", "keyvault", "scope");
    }

    @Test
    public void testDoAuthenticationRejectIfInvalidCertificateCredentialWithoutPassword() throws IOException {
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("KeyVault Authentication by certificate failed: keystore password was incorrect");
        final String path = new ClassPathResource("fakeCertificate.pfx").getFile().getAbsolutePath();
        keyVaultCredentialViaCertificate = new KeyVaultCertificateCredentials(
                "fakeClientId", path, "", 30);
        keyVaultCredentialViaCertificate.doAuthenticate("https://fakeauthorizationurl.com", "keyvault", "scope");
    }

    @Test
    public void testDoAuthenticationRejectIfInvalidCertificateCredentialWithPassword() throws IOException {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Uri should have at least one segment in the path");
        final String path = new ClassPathResource("fakeCertificate.pfx").getFile().getAbsolutePath();
        keyVaultCredentialViaCertificate = new KeyVaultCertificateCredentials(
                "fakeClientId", path, "topsecret", 30);
        keyVaultCredentialViaCertificate.doAuthenticate("https://fakeauthorizationurl.com", "keyvault", "scope");
    }

    @Test
    public void testMsiDoAuthentication() {
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Unable to get Token from MSI: Failed to connect to localhost");
        keyVaultMsiCredentials = new KeyVaultMsiCredentials();
        keyVaultMsiCredentials.doAuthenticate("https://fakeauthorizationurl.com", "https://vault.azure.net", "scope");
    }
}
