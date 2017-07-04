package com.microsoft.azure.spring.data.documentdb.repository.support;

import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import java.io.Serializable;


public class DocumentDbRepositoryFactory extends RepositoryFactorySupport {

    private final ApplicationContext applicationContext;

    public DocumentDbRepositoryFactory(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;

    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        return SimpleDocumentDbRepository.class;
    }

    @Override
    protected Object getTargetRepository(RepositoryInformation information) {
        return getTargetRepository((RepositoryMetadata) information);
    }

    protected Object getTargetRepository(RepositoryMetadata metadata) {

        final EntityInformation<?, Serializable> entityInformation = getEntityInformation(metadata.getDomainType());

        final Object repository = new SimpleDocumentDbRepository(
                (DocumentDbEntityInformation) entityInformation, this.applicationContext);

        return repository;
    }

    @Override
    public <T, ID extends Serializable> EntityInformation<T, ID> getEntityInformation(Class<T> domainClass) {
        return new DocumentDbEntityInformation<T, ID>(domainClass);
    }
}
