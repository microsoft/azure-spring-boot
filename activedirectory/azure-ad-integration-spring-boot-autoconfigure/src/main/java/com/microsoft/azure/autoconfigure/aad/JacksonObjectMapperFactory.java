/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.autoconfigure.aad;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonObjectMapperFactory {

    private JacksonObjectMapperFactory(){}

    private static class SingletonHelper{
            private static final ObjectMapper INSTANCE = new ObjectMapper();
    }
    public static ObjectMapper getInstance(){
        return SingletonHelper.INSTANCE;
    }
}
