package com.microsoft.azure.spring.data.documentdb.autoconfig;

import com.microsoft.azure.spring.data.documentdb.repository.DocumentDbRepository;
import com.microsoft.azure.spring.data.documentdb.repository.config.DocumentDbRepositoryConfigurationExtension;
import com.microsoft.azure.spring.data.documentdb.repository.support.DocumentDbRepositoryFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
@ConditionalOnClass({DocumentDbRepository.class})
@ConditionalOnMissingBean({DocumentDbRepositoryFactoryBean.class, DocumentDbRepositoryConfigurationExtension.class})
@ConditionalOnProperty(prefix = "azure.mongodb.repositories",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true)
@Import(DocumentDbRepositoriesAutoConfigureRegistrar.class)
public class DocumentDbRepositoriesAutoConfiguration {
}
