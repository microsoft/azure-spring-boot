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
