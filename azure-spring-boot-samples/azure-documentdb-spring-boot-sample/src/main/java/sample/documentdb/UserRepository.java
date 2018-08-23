/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package sample.documentdb;

import com.microsoft.azure.spring.data.cosmosdb.repository.DocumentDbRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends DocumentDbRepository<User, String> {
}
