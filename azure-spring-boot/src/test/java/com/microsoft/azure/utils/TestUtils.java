package com.microsoft.azure.utils;

public class TestUtils {
    /**
     *
     * @param propName property name
     * @param propValue value of property
     * @return property name and value pair. e.g., prop.name=prop.value
     */
    public static String propPair(String propName, String propValue) {
        return  propName + "=" + propValue;
    }
}
