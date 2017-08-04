/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.autoconfigure.aad;

import java.util.List;

public class CustomPermissionEvaluator {

    public static boolean hasPermission(List<DirectoryServiceObject> customerRolesGroups, List<String> targetRolesGroups) {
        boolean permitted = false;
        for (DirectoryServiceObject rg : customerRolesGroups) {
            if (targetRolesGroups.contains(rg.getDisplayName())) {
                permitted = true;
                break;
            }
        }
        return permitted;
    }
}
