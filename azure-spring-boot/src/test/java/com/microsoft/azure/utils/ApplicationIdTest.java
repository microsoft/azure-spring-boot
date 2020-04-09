/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.utils;

import org.junit.Assert;
import org.junit.Test;

import static com.microsoft.azure.keyvault.spring.Constants.SPRINGBOOT_KEY_VAULT_APPLICATION_ID;

public class ApplicationIdTest {

    @Test
    public void maxLength() {
        Assert.assertTrue(SPRINGBOOT_KEY_VAULT_APPLICATION_ID.length() <= 24);
    }

}
