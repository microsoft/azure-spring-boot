/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.keyvault.spring;

import com.microsoft.aad.adal4j.AsymmetricKeyCredential;
import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.azure.keyvault.authentication.KeyVaultCredentials;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.concurrent.*;


public class KeyVaultCertificateCredentials extends KeyVaultCredentials {

    private static final long DEFAULT_TOKEN_ACQUIRE_TIMEOUT_IN_SECONDS = 60L;
    private final String clientId;
    private final String pfxPath;
    private final String pfxPassword;
    private final long timeoutInSeconds;

    public KeyVaultCertificateCredentials(String clientId, String pfxPath,
                                          String pfxPassword, long timeoutInSeconds) {
        this.clientId = clientId;
        this.pfxPath = pfxPath;
        this.pfxPassword = pfxPassword;
        this.timeoutInSeconds = timeoutInSeconds;
    }

    public KeyVaultCertificateCredentials(String clientId, String pfxPath, String pfxPassword) {

        this(clientId, pfxPath, pfxPassword, DEFAULT_TOKEN_ACQUIRE_TIMEOUT_IN_SECONDS);
    }

    @Override
    public String doAuthenticate(String authorization, String resource, String scope) {
        try {
            final KeyCert certificateKey = readPfx(pfxPath, pfxPassword);
            final PrivateKey privateKey = certificateKey.getKey();

            final AuthenticationContext context = new AuthenticationContext(
                    authorization, false, Executors.newSingleThreadExecutor());

            final AsymmetricKeyCredential asymmetricKeyCredential = AsymmetricKeyCredential
                    .create(clientId, privateKey, certificateKey.getCertificate());

            final Future<AuthenticationResult> future = context
                    .acquireToken(resource, asymmetricKeyCredential, null);

            final AuthenticationResult result = future
                    .get(timeoutInSeconds, TimeUnit.SECONDS);
            return result.getAccessToken();
        } catch (InterruptedException | ExecutionException | IOException | CertificateException |
                NoSuchAlgorithmException | UnrecoverableKeyException | NoSuchProviderException |
                KeyStoreException | TimeoutException e) {
            throw new RuntimeException("KeyVault Authentication by certificate failed: " + e.getMessage());
        }
    }

    public KeyCert readPfx(String path, String password)
            throws NoSuchProviderException, KeyStoreException, IOException,
            NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException {

        try (FileInputStream stream = new FileInputStream(path)){
            final KeyStore store = KeyStore.getInstance(KeyStore.getDefaultType());
            store.load(stream, password.toCharArray());
            final KeyCert keyCert = new KeyCert();
            final Enumeration<String> aliases = store.aliases();
            final String alias = aliases.nextElement();
            final X509Certificate certificate = (X509Certificate) store.getCertificate(alias);
            final PrivateKey key = (PrivateKey) store.getKey(alias, password.toCharArray());
            keyCert.setCertificate(certificate);
            keyCert.setKey(key);
            return keyCert;
        }
    }

    public static class KeyCert {

        private X509Certificate certificate;
        private PrivateKey key;

        public X509Certificate getCertificate() {
            return certificate;
        }

        public void setCertificate(X509Certificate certificate) {
            this.certificate = certificate;
        }

        public PrivateKey getKey() {
            return key;
        }

        public void setKey(PrivateKey key) {
            this.key = key;
        }
    }
}
