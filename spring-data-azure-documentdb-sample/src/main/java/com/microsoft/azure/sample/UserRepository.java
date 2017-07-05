package com.microsoft.azure.sample;


import com.microsoft.azure.spring.data.documentdb.repository.DocumentDbRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends DocumentDbRepository<User, String> {
}

