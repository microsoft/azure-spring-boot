package com.microsoft.azure.spring.data.documentdb.repository.domain;

import com.microsoft.azure.spring.data.documentdb.repository.DocumentDbRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends DocumentDbRepository<Person, String> {
}
