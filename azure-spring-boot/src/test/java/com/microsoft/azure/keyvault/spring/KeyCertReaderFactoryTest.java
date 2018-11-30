/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.keyvault.spring;

import com.microsoft.azure.keyvault.spring.certificate.KeyCertReader;
import com.microsoft.azure.keyvault.spring.certificate.KeyCertReaderFactory;
import com.microsoft.azure.keyvault.spring.certificate.PfxCertReader;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class KeyCertReaderFactoryTest {
    private static final String FAKE_PFX_CERT = "fake-pfx-cert.pfx";

    @Test
    public void testPfxCertificateReader() {
        final KeyCertReader certReader = KeyCertReaderFactory.getReader(FAKE_PFX_CERT);
        assertThat(certReader).isInstanceOf(PfxCertReader.class);
    }
}
