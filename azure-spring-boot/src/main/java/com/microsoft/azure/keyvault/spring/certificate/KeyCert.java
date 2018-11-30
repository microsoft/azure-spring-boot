/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.keyvault.spring.certificate;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

@Getter
@AllArgsConstructor
public class KeyCert {
    private final X509Certificate certificate;
    private final PrivateKey key;
}
