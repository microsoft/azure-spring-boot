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
import com.microsoft.azure.keyvault.spring.certificate.KeyCert;
import com.microsoft.azure.keyvault.spring.certificate.KeyCertReader;
import com.microsoft.azure.keyvault.spring.certificate.KeyCertReaderFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.MalformedURLException;
import java.security.PrivateKey;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
public class KeyVaultCertificateCredential extends KeyVaultCredentials {
    private static final long DEFAULT_TOKEN_ACQUIRE_TIMEOUT_IN_SECONDS = 60L;
    private final String clientId;
    private final String certPath;
    private final String certPassword;
    private final long timeoutInSeconds;

    public KeyVaultCertificateCredential(String clientId, String certPath, String certPassword, long timeoutInSeconds) {
        this.clientId = clientId;
        this.certPath = certPath;
        this.certPassword = certPassword;
        this.timeoutInSeconds = timeoutInSeconds <= 0 ? DEFAULT_TOKEN_ACQUIRE_TIMEOUT_IN_SECONDS : timeoutInSeconds;
    }

    @Override
    public String doAuthenticate(String authorization, String resource, String scope) {
        final KeyCertReader certReader = KeyCertReaderFactory.getReader(certPath);
        File certfile = new File(certPath);
        if (!certfile.exists()) {
            certfile = new File(getClass().getClassLoader().getResource(certPath).getFile());
        }

        if (!certfile.exists()) {
            throw new IllegalStateException(String.format("Certificate file %s not found.", certPath));
        }

        final KeyCert keyCert = certReader.read(certfile, certPassword);
        final PrivateKey privateKey = keyCert.getKey();

        try {
            final AuthenticationContext context = new AuthenticationContext(authorization, false,
                    Executors.newSingleThreadExecutor());

            final AsymmetricKeyCredential asymmetricKeyCredential = AsymmetricKeyCredential.create(clientId, privateKey,
                    keyCert.getCertificate());

            final AuthenticationResult authResult = context.acquireToken(resource, asymmetricKeyCredential, null)
                            .get(timeoutInSeconds, TimeUnit.SECONDS);

            return authResult.getAccessToken();
        } catch (MalformedURLException | InterruptedException | ExecutionException | TimeoutException e) {
            log.error("Failed to do authentication", e);
            throw new IllegalStateException(e);
        }
    }
}
