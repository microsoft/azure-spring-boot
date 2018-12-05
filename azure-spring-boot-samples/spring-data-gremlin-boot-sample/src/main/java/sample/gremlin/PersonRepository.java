/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package sample.gremlin;

import com.microsoft.spring.data.gremlin.repository.GremlinRepository;

public interface PersonRepository extends GremlinRepository<Person, String> {

// TODO(pan): will enable this after gremlin add lookup strategy.
//    List<Person> findByNameAndLevel(String name, int level);
}
