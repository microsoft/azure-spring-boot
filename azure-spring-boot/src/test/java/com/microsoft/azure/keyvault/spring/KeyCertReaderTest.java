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
    private static final String TEST_PFX_PASSWORD = "123456";

    @Test
    public void testPfxCertReaderCanRead() {
        final Resource resource = new DefaultResourceLoader().getResource(TEST_PFX_FILE);
        final PfxCertReader reader = new PfxCertReader();
        final KeyCert pfxCert = reader.read(resource, TEST_PFX_PASSWORD);

        assertThat(pfxCert).isNotNull();
        assertThat(pfxCert.getCertificate()).isNotNull();
        assertThat(pfxCert.getKey()).isNotNull();
    }

}
