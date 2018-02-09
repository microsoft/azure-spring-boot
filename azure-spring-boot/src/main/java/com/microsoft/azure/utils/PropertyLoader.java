/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyLoader {
    private static final String PROJECT_PROPERTY_FILE = "/META-INF/project.properties";

    public static String getProjectVersion() {
        String version = "unknown";
        InputStream inputStream = null;
        try {
            inputStream = PropertyLoader.class.getResourceAsStream(PROJECT_PROPERTY_FILE);
            if (inputStream != null) {
                final Properties properties = new Properties();
                properties.load(inputStream);

                version = properties.getProperty("project.version");
            }
        } catch (IOException e) {
            // Omitted
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // Omitted
                }
            }
        }

        return version;
    }
}
