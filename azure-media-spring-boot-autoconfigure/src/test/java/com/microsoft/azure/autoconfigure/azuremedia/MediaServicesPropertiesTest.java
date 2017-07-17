package com.microsoft.azure.autoconfigure.azuremedia;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MediaServicesPropertiesTest {

    private static final String MEDIA_SERVICE_URI = "some media service URI";
    private static final String MEDIA_SERVICE_PROPERTY = "azure.media.mediaServiceUri";
    private static final String OAUTH_URI = "some oauth URI";
    private static final String OAUTH_URI_PROPERTY = "azure.media.oAuthUri";
    private static final String CLIENTID = "some clientId";
    private static final String CLIENTID_PROPERTY = "azure.media.clientId";
    private static final String CLIENTSECRET = "some clientSecret";
    private static final String CLIENTSECRET_PROPERTY = "azure.media.clientSecret";
    private static final String SCOPE = "some oauth URI";
    private static final String SCOPE_PROPERTY = "azure.media.scope";

    @Test
    public void canSetProperties() {
        System.setProperty(MEDIA_SERVICE_PROPERTY, MEDIA_SERVICE_URI);
        System.setProperty(OAUTH_URI_PROPERTY, OAUTH_URI);
        System.setProperty(CLIENTID_PROPERTY, CLIENTID);
        System.setProperty(CLIENTSECRET_PROPERTY, CLIENTSECRET);
        System.setProperty(SCOPE_PROPERTY, SCOPE);

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(Config.class);
            context.refresh();

            final MediaServicesProperties properties = context
                    .getBean(MediaServicesProperties.class);
            assertThat(properties.getMediaServiceUri()).isEqualTo(MEDIA_SERVICE_URI);
            assertThat(properties.getoAuthUri()).isEqualTo(OAUTH_URI);
            assertThat(properties.getClientId()).isEqualTo(CLIENTID);
            assertThat(properties.getClientSecret()).isEqualTo(CLIENTSECRET);
            assertThat(properties.getScope()).isEqualTo(SCOPE);
        }
        System.clearProperty(MEDIA_SERVICE_PROPERTY);
    }

    @Test
    public void emptySettingNotAllowed() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(Config.class);

            Exception exception = null;
            try {
                context.refresh();
            }
            catch (Exception e) {
                exception = e;
            }

            assertThat(exception).isNotNull();
            assertThat(exception).isExactlyInstanceOf(BeanCreationException.class);
            assertThat(exception.getCause().getMessage()).contains(
                    "Field error in object 'azure.media' on field 'clientSecret': "
                            + "rejected value [null];");
        }
    }

    @TestConfiguration
    @EnableConfigurationProperties(MediaServicesProperties.class)
    static class Config {
    }
}
