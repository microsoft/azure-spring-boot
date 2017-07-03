package com.microsoft.azure.spring.data.documentdb.repository;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.io.Serializable;
import java.util.List;

@NoRepositoryBean
public interface DocumentDbRepository<T, ID extends Serializable> extends PagingAndSortingRepository<T, ID> {

    @Override
    List<T> findAll();
}

