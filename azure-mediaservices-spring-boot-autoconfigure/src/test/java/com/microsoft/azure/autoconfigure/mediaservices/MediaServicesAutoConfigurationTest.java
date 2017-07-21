package com.microsoft.azure.autoconfigure.mediaservices;

import com.microsoft.windowsazure.services.media.MediaContract;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MediaServicesAutoConfigurationTest {
    @Test
    public void createMediaServiceAccount() {
        System.setProperty(Constants.ACCOUNT_KEY_PROPERTY, Constants.ACCOUNT_KEY);
        System.setProperty(Constants.ACCOUNT_NAME_PROPERTY, Constants.ACCOUNT_NAME);

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(MediaServicesAutoConfiguration.class);
            context.refresh();

            final MediaContract mediaContract = context.getBean(MediaContract.class);
            assertThat(mediaContract).isNotNull();
        }

        System.clearProperty(Constants.ACCOUNT_KEY_PROPERTY);
        System.clearProperty(Constants.ACCOUNT_NAME_PROPERTY);
    }

}
