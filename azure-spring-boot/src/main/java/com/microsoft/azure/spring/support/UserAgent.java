/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.support;

public class UserAgent {
    /**
     * Generate UserAgent string for given service.
     *
     * @param serviceName Name of the service from which called this method.
     * @param allowTelemetry Whether allows telemtry
     * @return generated UserAgent string
     */
    public static String getUserAgent(String serviceName, boolean allowTelemetry) {
        final String os = System.getProperty("os.name") + "/" + System.getProperty("os.version");

        String macAddress = "Not Collected";
        if (allowTelemetry) {
            macAddress = GetHashMac.getHashMac();
        }

        final String javaVersion = System.getProperty("java.version");

        return String.format(serviceName + " OS:%s MacAddressHash:%s Java:%s", os, macAddress, javaVersion);
    }
}
