package com.microsoft.azure.spring.data.documentdb.config;

import com.microsoft.azure.documentdb.DocumentClient;
import com.microsoft.azure.spring.data.documentdb.DocumentDbFactory;
import com.microsoft.azure.spring.data.documentdb.core.DocumentDbTemplate;
import com.microsoft.azure.spring.data.documentdb.core.convert.DocumentDbConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public abstract class AbstractDocumentDbConfiguration extends DocumentDbConfigurationSupport {

    public abstract String getDatabase();

    public abstract DocumentClient documentClient();

    @Bean
    public DocumentDbFactory documentDbFactory() throws Exception {
        return new DocumentDbFactory(this.documentClient());
    }

    @Bean
    public DocumentDbConverter documentDbConverter() {
        return new DocumentDbConverter();
    }

    @Bean
    public DocumentDbTemplate documentDbTemplate() throws Exception {
        return new DocumentDbTemplate(this.documentDbFactory(), this.documentDbConverter(), this.getDatabase());
    }

    protected String getMappingBasePackage() {
        final Package mappingBasePackage = getClass().getPackage();
        return mappingBasePackage == null ? null : mappingBasePackage.getName();
    }

}
