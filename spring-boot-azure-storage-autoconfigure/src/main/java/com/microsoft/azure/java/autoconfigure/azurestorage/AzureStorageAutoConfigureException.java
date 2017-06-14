/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */
package com.microsoft.azure.java.autoconfigure.azurestorage;


import org.springframework.core.NestedRuntimeException;

public class AzureStorageAutoConfigureException extends NestedRuntimeException {
    public AzureStorageAutoConfigureException(String msg, Throwable t) {
        super(msg, t);
    }
}
