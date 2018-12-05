/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.gremlin.domain;

import com.microsoft.spring.data.gremlin.repository.GremlinRepository;

public interface UserRepository extends GremlinRepository<User, String> {

// TODO(pan): will enable this after gremlin add lookup strategy.
//    List<User> findByNameAndEnabled(String name, Boolean enabled);
}
