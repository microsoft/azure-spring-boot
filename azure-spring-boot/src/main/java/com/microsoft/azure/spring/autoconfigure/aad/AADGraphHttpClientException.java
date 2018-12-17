/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.aad;

/**
 * Thrown for any unsuccessful HTTP REST calls within the {@link AADGraphHttpClient}
 */
class AADGraphHttpClientException extends Exception {
    AADGraphHttpClientException(String message, Throwable cause) {
        super(message, cause);
    }

    AADGraphHttpClientException(String message) {
        super(message);
    }
}
