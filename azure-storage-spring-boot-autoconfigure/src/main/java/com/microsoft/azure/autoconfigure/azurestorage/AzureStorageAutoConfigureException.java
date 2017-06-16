/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */
package com.microsoft.azure.autoconfigure.azurestorage;

public class AzureStorageAutoConfigureException extends RuntimeException {
    public AzureStorageAutoConfigureException(String msg, Throwable t) {
        super(msg, t);
    }
}
