/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.sqlserver;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

@ConfigurationProperties(prefix = "spring.datasource")
public class AlwaysEncryptedDataSourceProperties extends DataSourceProperties {

    @Override
    public void afterPropertiesSet() throws Exception {
       super.afterPropertiesSet();
       this.setUrl(determineUrl());
    }

    @Override
    public String determineUrl() {
        if (!StringUtils.isEmpty(this.getUrl())) {
            return this.getUrl() + ";" + AEConstants.PROPERTY_ENCRYPTION_ENABLED_VALUE;
        }
        return super.determineUrl();
    }
}
