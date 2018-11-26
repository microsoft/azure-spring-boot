/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.keyvault.spring.certificate;

import org.apache.commons.io.FilenameUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Factory to create different certificate reader based on extension.
 */
public class KeyCertReaderFactory {
    private static final ConcurrentMap<String, KeyCertReader> readerMap = new ConcurrentHashMap<>();
    private static final String NOT_SUPPORTED_CERT = "Certificate type %s not supported.";
    private static final String PFX_EXTENSION = "pfx";

    public static KeyCertReader getReader(String certFile) {
        final String extension = FilenameUtils.getExtension(certFile);

        switch (extension) {
            case PFX_EXTENSION:
                readerMap.putIfAbsent(PFX_EXTENSION, new PfxCertReader());
                return readerMap.get(PFX_EXTENSION);
            default:
                throw new IllegalStateException(String.format(NOT_SUPPORTED_CERT, extension));
        }
    }
}
