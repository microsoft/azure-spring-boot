/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.keyvault.spring;

import com.microsoft.azure.keyvault.spring.certificate.KeyCert;
import com.microsoft.azure.keyvault.spring.certificate.PfxCertReader;
import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import static org.assertj.core.api.Assertions.assertThat;

public class KeyCertReaderTest {
    private static final String TEST_PFX_FILE = "testkeyvault.pfx";
    private static final String TEST_SUBJECT = "CN=testkeyvault";
    private static final String TEST_PFX_PASSWORD = "123456";

    private static final String TEST_NO_PASSWORD_PFX_FILE = "nopwdcert.pfx";
    private static final String TEST_NO_PASSWORD_SUBJECT = "CN=nopwdcert";
    private static final String TEST_NO_PASSWORD = null;

    @Test
    public void testPfxCertReaderCanRead() {
        validatePfxCertRead(TEST_PFX_FILE, TEST_PFX_PASSWORD, TEST_SUBJECT);
    }

    @Test
    public void testPfxCertNoPasswordReaderCanRead() {
        validatePfxCertRead(TEST_NO_PASSWORD_PFX_FILE, TEST_NO_PASSWORD, TEST_NO_PASSWORD_SUBJECT);
    }

    private void validatePfxCertRead(String file, String password, String expectedSubject) {
        final Resource resource = new DefaultResourceLoader().getResource(file);
        final PfxCertReader reader = new PfxCertReader();
        final KeyCert pfxCert = reader.read(resource, password);

        assertThat(pfxCert).isNotNull();
        assertThat(pfxCert.getCertificate()).isNotNull();
        assertThat(pfxCert.getCertificate().getSubjectX500Principal().getName()).isEqualTo(expectedSubject);
        assertThat(pfxCert.getKey()).isNotNull();
    }
}
