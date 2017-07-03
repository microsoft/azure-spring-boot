package com.microsoft.azure.spring.data.documentdb.repository.support;

import org.springframework.data.repository.core.support.AbstractEntityInformation;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;


public class DocumentDbEntityInformation<T, ID extends Serializable>
        extends AbstractEntityInformation<T, ID> {

    private Field id;
    private String collectionName;

    public DocumentDbEntityInformation(Class<T> domainClass) {
        super(domainClass);

        this.id = ReflectionUtils.findField(getJavaType(), "id");
        if (this.id == null) {
            throw new IllegalArgumentException("entity must contains id field");
        }
        ReflectionUtils.makeAccessible(this.id);

        this.collectionName = domainClass.getSimpleName();
    }

    public ID getId(T entity) {
        return (ID) ReflectionUtils.getField(id, entity);
    }

    public Class<ID> getIdType() {
        return (Class<ID>) id.getType();
    }

    public String getCollectionName() {
        return this.collectionName;
    }
}
