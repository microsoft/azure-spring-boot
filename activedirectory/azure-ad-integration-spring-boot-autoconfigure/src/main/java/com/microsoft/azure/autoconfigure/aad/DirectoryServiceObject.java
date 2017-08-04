/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.autoconfigure.aad;

public class DirectoryServiceObject {
    private final String OdataType;
    private final String ObjectType;
    private final String Description;
    private final String DisplayName;

    public DirectoryServiceObject(String odataType, String objectType, String description, String displayName) {
        OdataType = odataType;
        ObjectType = objectType;
        Description = description;
        DisplayName = displayName;
    }
    public String getOdataType() {
        return OdataType;
    }
    public String getObjectType() {
        return ObjectType;
    }
    public String getDisplayName() {
        return DisplayName;
    }
    public String getDescription() {
        return Description;
    }
}
