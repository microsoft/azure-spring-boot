package com.microsoft.azure.autoconfigure.mediaservices;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.microsoft.windowsazure.services.media.MediaContract;
import com.microsoft.windowsazure.services.media.implementation.MediaExceptionProcessor;

public class MediaServicesAutoConfigurationTest {

    private static final String CONNECTION_ACCOUNTKEY_PROPERTY = "azure.mediaservices.account-key";
    private static final String CONNECTION_ACCOUNTNAME_PROPERTY = "azure.mediaservices.account-name";
    private static final String ACCOUNT_KEY = "someKey";
    private static final String ACCOUNT_NAME = "someName";

    @Test
    public void createMediaServiceAccount() {
        System.setProperty(CONNECTION_ACCOUNTKEY_PROPERTY, ACCOUNT_KEY);
        System.setProperty(CONNECTION_ACCOUNTNAME_PROPERTY, ACCOUNT_NAME);

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(MediaServicesAutoConfiguration.class);
            context.refresh();

            MediaContract mediaContract = null;
            try {
                mediaContract = context.getBean(MediaContract.class);
            } catch (Exception e) {
                assertThat(e).isExactlyInstanceOf(BeanCreationException.class);
            }

            assertThat(mediaContract).isNotNull();
            assertThat(mediaContract).isExactlyInstanceOf(MediaExceptionProcessor.class);
        }

        System.clearProperty(CONNECTION_ACCOUNTKEY_PROPERTY);
        System.clearProperty(CONNECTION_ACCOUNTNAME_PROPERTY);
    }

}
