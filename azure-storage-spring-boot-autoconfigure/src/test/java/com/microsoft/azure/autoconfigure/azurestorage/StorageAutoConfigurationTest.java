/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.autoconfigure.azurestorage;

import com.microsoft.azure.storage.CloudStorageAccount;
import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

public class StorageAutoConfigurationTest {
    private static final String INVALID_CONNECTION_STRING = "invalid connection string";
    private static final String CONNECTION_STRING_WITH_VALID_FORMAT = "DefaultEndpointsProtocol=https;" +
            "AccountName=account-name;" +
            "AccountKey=9ycDniQThM+dT18TmcfhgLyHQCKju9/B9VOtTQ4BOLPhpVbWXbyf9zvNTGe7LB3p2zm5Yl89IQyNgLWw1Wnjxzzj;" +
            "EndpointSuffix=core.windows.net";

    @Test
    public void createStorageAccountWithInvalidConnectionString() {
        System.setProperty("azure.storage.connection-string", INVALID_CONNECTION_STRING);

        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(StorageAutoConfiguration.class);
        context.refresh();

        CloudStorageAccount cloudStorageAccount = null;
        try {
            cloudStorageAccount = context.getBean(CloudStorageAccount.class);
        } catch (Exception e) {
            assertThat(e).isExactlyInstanceOf(BeanCreationException.class);
        }

        assertThat(cloudStorageAccount).isNull();
    }

    @Test
    public void createStorageAccountWithValidConnectionStringFormat() {
        System.setProperty("azure.storage.connection-string", CONNECTION_STRING_WITH_VALID_FORMAT);

        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(StorageAutoConfiguration.class);
        context.refresh();

        final CloudStorageAccount cloudStorageAccount = context.getBean(CloudStorageAccount.class);
        assertThat(cloudStorageAccount).isNotNull();
    }
}
