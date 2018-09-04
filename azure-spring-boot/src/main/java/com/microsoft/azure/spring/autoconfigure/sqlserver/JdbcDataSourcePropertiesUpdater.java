/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.sqlserver;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.util.Assert;

public class JdbcDataSourcePropertiesUpdater {

    public void updateDataSourceProperties(DataSourceProperties dataSourceProperties) {
        Assert.hasText(dataSourceProperties.getUrl(), "spring.datasource.url must not be empty");

        final String jdbcUrl = dataSourceProperties.getUrl();
        // Set Property to enable Encryption
        dataSourceProperties.setUrl(jdbcUrl + ";" + AEConstants.PROPERTY_ENCRYPTION_ENABLED_VALUE);
    }
}
