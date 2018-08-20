/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.sqlserver;

public class AEConstants {
    public static final String PROPERTY_AE_ENABLED = "spring.datasource.alwaysencrypted";
    public static final String PROPERTY_DATASOURCE_COL_ENCRYPT =
                       "spring.datasource.dataSourceProperties.ColumnEncryptionSetting";
    public static final String PROPERTY_CONNECTION_COL_ENCRYPT = "spring.datasource.connectionProperties";

}
