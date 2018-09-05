/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Memorize function execution result
 *
 * @author Warren Zhu
 */
public class Memoizer {
    public static <T, R> Function<T, R> memoize(Function<T, R> fn) {
        final Map<T, R> map = new ConcurrentHashMap<>();
        return (t) -> map.computeIfAbsent(t, fn);
    }

    public static <T, R> Function<T, R> memoize(Map<T, R> map, Function<T, R> fn) {
        return (t) -> map.computeIfAbsent(t, fn);
    }
}
