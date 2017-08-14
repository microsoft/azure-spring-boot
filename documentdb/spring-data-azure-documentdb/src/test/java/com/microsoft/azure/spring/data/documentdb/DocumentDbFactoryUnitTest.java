/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.data.documentdb;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class DocumentDbFactoryUnitTest {

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidKey() throws Exception {
        new DocumentDbFactory("https://fakeuri", null);
    }
}
