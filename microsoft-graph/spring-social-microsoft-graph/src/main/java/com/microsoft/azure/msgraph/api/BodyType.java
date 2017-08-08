/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.msgraph.api;

/**
 * The Enum Body Type.
 */
public enum BodyType
{
    /**
     * text
     */
    text,
    /**
     * html
     */
    html,
    /**
     * For BodyType values that were not expected from the service
     */
    unexpectedValue
}