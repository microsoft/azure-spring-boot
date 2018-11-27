/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.keyvault.spring.certificate;

import org.apache.tinkerpop.shaded.kryo.io.Input;
import org.springframework.core.io.Resource;

import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

public class PfxCertReader implements KeyCertReader {
    private String CERT_NOT_FOUND = "Cert file %s not found.";
    private String CERT_READ_FAILURE = "Failed to read cert file %s.";

    @Override
    public KeyCert read(Resource resource, String password) {
        try (InputStream inputStream = resource.getInputStream()) {
            KeyCert keyCert = null;

            final KeyStore store = KeyStore.getInstance("pkcs12", "SunJSSE");
            final char[] passwordArray = password == null ? null : password.toCharArray();
            store.load(inputStream, passwordArray);

            final Enumeration<String> aliases = store.aliases();
            while (aliases.hasMoreElements()) {
                final String alias = aliases.nextElement();

                if (store.getCertificate(alias).getType().equals("X.509") && store.isKeyEntry(alias)) {
                    final X509Certificate certificate = (X509Certificate) store.getCertificate(alias);
                    final PrivateKey key = (PrivateKey) store.getKey(alias, passwordArray);

                    keyCert = new KeyCert(null, null);
                    keyCert.setCertificate(certificate);
                    keyCert.setKey(key);

                    break;
                }
            }

            if (keyCert == null) {
                throw new IllegalStateException(String.format(CERT_READ_FAILURE, resource.getFilename()));
            }

            return keyCert;
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(String.format(CERT_NOT_FOUND, resource.getFilename()), e);
        } catch (IOException | KeyStoreException | NoSuchProviderException | NoSuchAlgorithmException |
                UnrecoverableKeyException | CertificateException e) {
            throw new IllegalStateException(String.format(CERT_READ_FAILURE, resource.getFilename()), e);
        }
    }
}
