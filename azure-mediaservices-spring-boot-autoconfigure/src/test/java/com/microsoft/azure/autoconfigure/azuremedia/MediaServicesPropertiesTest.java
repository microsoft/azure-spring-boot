package com.microsoft.azure.autoconfigure.azuremedia;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

import com.microsoft.azure.autoconfigure.mediaservices.MediaServicesProperties;

public class MediaServicesPropertiesTest {

    private static final String ACCOUNT_NAME = "some accountname";
    private static final String ACCOUNT_NAME_PROPERTY = "azure.mediaservices.accountName";
    private static final String ACCOUNT_KEY = "some accountKey";
    private static final String ACCOUNT_KEY_PROPERTY = "azure.mediaservices.accountKey";

    @Test
    public void canSetProperties() {
        System.setProperty(ACCOUNT_NAME_PROPERTY, ACCOUNT_NAME);
        System.setProperty(ACCOUNT_KEY_PROPERTY, ACCOUNT_KEY);

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(Config.class);
            context.refresh();

            final MediaServicesProperties properties = context
                    .getBean(MediaServicesProperties.class);
            assertThat(properties.getMediaServiceUri())
                    .isEqualTo("https://media.windows.net/API/");
            assertThat(properties.getoAuthUri()).isEqualTo(
                    "https://wamsprodglobal001acs.accesscontrol.windows.net/v2/OAuth2-13");
            assertThat(properties.getAccountName()).isEqualTo(ACCOUNT_NAME);
            assertThat(properties.getAccountKey()).isEqualTo(ACCOUNT_KEY);
            assertThat(properties.getScope()).isEqualTo("urn:WindowsAzureMediaServices");
        }
        System.clearProperty(ACCOUNT_NAME_PROPERTY);
        System.clearProperty(ACCOUNT_KEY_PROPERTY);
    }

    @Test
    public void emptySettingNotAllowed() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(Config.class);

            Exception exception = null;
            try {
                context.refresh();
            } catch (Exception e) {
                exception = e;
            }

            assertThat(exception).isNotNull();
            assertThat(exception).isExactlyInstanceOf(BeanCreationException.class);
            assertThat(exception.getCause().getMessage()).contains(
                    "Field error in object 'azure.mediaservices' on field 'accountName': "
                            + "rejected value [null];");
        }
    }

    @Configuration
    @EnableConfigurationProperties(MediaServicesProperties.class)
    static class Config {
    }
}
