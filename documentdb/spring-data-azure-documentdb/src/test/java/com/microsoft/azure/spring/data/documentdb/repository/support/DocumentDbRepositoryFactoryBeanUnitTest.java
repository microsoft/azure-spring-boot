/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.data.documentdb.repository.support;

import com.microsoft.azure.spring.data.documentdb.domain.PersonRepository;
import org.junit.Test;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import static org.assertj.core.api.Assertions.assertThat;

public class DocumentDbRepositoryFactoryBeanUnitTest {
    @Test
    public void testCreateRepositoryFactory() {
        final DocumentDbRepositoryFactoryBean factoryBean = new DocumentDbRepositoryFactoryBean(PersonRepository.class);
        final RepositoryFactorySupport factory = factoryBean.createRepositoryFactory();
        assertThat(factory).isNotNull();
    }
}
