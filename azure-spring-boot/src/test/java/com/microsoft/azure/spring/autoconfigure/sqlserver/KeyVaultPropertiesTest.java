package com.microsoft.azure.spring.autoconfigure.sqlserver;

import com.microsoft.azure.spring.autoconfigure.aad.Constants;
import com.microsoft.azure.spring.autoconfigure.documentdb.DocumentDBPropertiesTest;
import org.junit.After;
import org.junit.Test;
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
    private static final String CLIENT_SECRET_PROPERTY = "azure.sqlserver.vault.client-secret";
    private static final String CLIENT_ID_PROPERTY = "azure.sqlserver.vault.client-id";


    @After
    public void clearAllProperties() {
        System.clearProperty(Constants.CLIENT_SECRET_PROPERTY);
        System.clearProperty(Constants.CLIENT_ID_PROPERTY);
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
            assertThat(exception).isExactlyInstanceOf(ConfigurationPropertiesBindException.class);

            final BindValidationException bindException = (BindValidationException) exception.getCause().getCause();
            final List<ObjectError> errors = bindException.getValidationErrors().getAllErrors();
            final List<String> errorStrings = errors.stream().map(e -> e.toString()).collect(Collectors.toList());

            Collections.sort(errorStrings);

            final List<String> errorStringsExpected = Arrays.asList(
                    "Field error in object 'azure.documentdb' on field 'key': rejected value [null];",
                    "Field error in object 'azure.documentdb' on field 'uri': rejected value [null];"
            );

            assertThat(errorStrings.size()).isEqualTo(errorStringsExpected.size());

            for (int i = 0; i < errorStrings.size(); i++) {
                assertThat(errorStrings.get(i)).contains(errorStringsExpected.get(i));
            }
        }
    }


    @Configuration
    @EnableConfigurationProperties(KeyVaultProperties.class)
    static class Config {
    }
}
