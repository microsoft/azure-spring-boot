/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.common;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class CommonTest {

    @Test
    public void testHashMACNotNull() {
        final String hashMac = GetMacAddress.getMacHash();

        assertThat(hashMac).isNotNull();
    }
}
