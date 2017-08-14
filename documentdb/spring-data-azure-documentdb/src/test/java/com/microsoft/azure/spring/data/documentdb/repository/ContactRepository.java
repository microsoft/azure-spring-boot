/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.data.documentdb.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends DocumentDbRepository<Contact, String> {
}
