/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.test.cosmosdb;

import com.microsoft.azure.spring.data.cosmosdb.core.mapping.Document;
import lombok.*;

@Document(collection = "mycollection")
@Data
@AllArgsConstructor
public class User {
    private String id;
    private String firstName;
    private String lastName;
    private String address;

    public User() {
    }

    @Override
    public String toString() {
        return String.format("%s %s, %s", firstName, lastName, address);
    }
}
