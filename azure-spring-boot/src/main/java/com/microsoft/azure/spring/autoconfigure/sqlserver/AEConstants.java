/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.sqlserver;

public class AEConstants {
    public static final String PROPERTY_AE_ENABLED = "spring.datasource.always-encrypted.enabled";
    public static final String PROPERTY_DATASOURCE_COL_ENCRYPT =
                       "spring.datasource.data-source-properties.ColumnEncryptionSetting";
    public static final String PROPERTY_CONNECTION_COL_ENCRYPT = "spring.datasource.connection-properties";
    public static final String PROPERTY_ENCRYPTION_ENABLED_VALUE = "ColumnEncryptionSetting=Enabled";
}
