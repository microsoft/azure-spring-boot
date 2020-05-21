/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.storage.utils;

public class LogUtils {

    public static void logInfo(String log, Object... params) {
        System.out.println(String.format(log, params));
    }

    public static void logError(String log, Object... params) {
        System.err.println(String.format(log, params));
    }

}
