package com.microsoft.azure.spring.autoconfigure.sqlserver;

import com.microsoft.azure.spring.autoconfigure.aad.Constants;
import com.microsoft.azure.spring.autoconfigure.documentdb.DocumentDBProperties;
import com.microsoft.azure.spring.autoconfigure.documentdb.DocumentDBPropertiesTest;
import com.microsoft.azure.spring.autoconfigure.documentdb.PropertySettingUtil;
import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.validation.BindValidationException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.ObjectError;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class KeyVaultPropertiesTest {
    private static final String CLIENT_SECRET_PROPERTY = "azure.sqlserver.keyvault.client-secret";
    private static final String CLIENT_ID_PROPERTY = "azure.sqlserver.keyvault.client-id";


    @After
    public void clearAllProperties() {
        System.clearProperty(CLIENT_SECRET_PROPERTY);
        System.clearProperty(CLIENT_ID_PROPERTY);
    }

    @Test
    public void canSetAllProperties() {
        System.setProperty(CLIENT_SECRET_PROPERTY,"secret");
        System.setProperty(CLIENT_ID_PROPERTY,"id");

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(KeyVaultPropertiesTest.Config.class);
            context.refresh();
            final KeyVaultProperties properties = context.getBean(KeyVaultProperties.class);

            assertThat(properties.getClientId()).isEqualTo(System.getProperty(CLIENT_ID_PROPERTY));
            assertThat(properties.getClientSecret()).isEqualTo(System.getProperty(CLIENT_SECRET_PROPERTY));
         }
    }

    @Test
    public void emptySettingNotAllowed() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            Exception exception = null;

            context.register(KeyVaultPropertiesTest.Config.class);

            try {
                context.refresh();
            } catch (Exception e) {
                exception = e;
            }

            assertThat(exception).isNotNull();
            assertThat(exception).isExactlyInstanceOf(BeanCreationException.class);


            final String errorStringExpected = "azure.sqlserver.keyvault.client-id must be provided";
            assertThat(exception.getMessage().contains(errorStringExpected));

        }
    }


    @Configuration
    @EnableConfigurationProperties(KeyVaultProperties.class)
    static class Config {
    }
}
