/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.msgraph.api;

public class ItemBody {
    private BodyType contentType;

    private String content;

    /**
     * The Content Type.
     */
    public BodyType getContentType() {
        return contentType;
    }

    public void setContentType(BodyType contentType) {
        this.contentType = contentType;
    }

    /**
     * The Content.
     */
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
