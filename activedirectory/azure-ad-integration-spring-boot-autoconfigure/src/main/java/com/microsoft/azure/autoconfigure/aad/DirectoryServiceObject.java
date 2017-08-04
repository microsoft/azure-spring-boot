/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.autoconfigure.aad;

public class DirectoryServiceObject {
    private String odataType;
    private String objectType;
    private String description;
    private String displayName;

    public DirectoryServiceObject(String odataType, String objectType, String description, String displayName) {
        this.odataType = odataType;
        this.objectType = objectType;
        this.description = description;
        this.displayName = displayName;
    }
    public String getOdataType() {
        return odataType;
    }
    public String getObjectType() {
        return objectType;
    }
    public String getDisplayName() {
        return displayName;
    }
    public String getDescription() {
        return description;
    }
}
