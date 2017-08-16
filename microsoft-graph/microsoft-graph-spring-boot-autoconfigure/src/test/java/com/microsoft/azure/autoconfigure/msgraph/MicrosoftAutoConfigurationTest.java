package com.microsoft.azure.autoconfigure.msgraph;

import com.microsoft.azure.msgraph.api.Microsoft;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.social.SocialWebAutoConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.fail;

public class MicrosoftAutoConfigurationTest {
    @Test
    public void canAutowire() {
        System.setProperty(Constants.APP_ID_PROPERTY, Constants.APP_ID);
        System.setProperty(Constants.APP_SCERET_PROPERTY, Constants.APP_SCERET);

        try (AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext()) {
            context.register(MicrosoftAutoConfiguration.class);
            context.register(SocialWebAutoConfiguration.class);
            context.refresh();
            Assertions.assertThat(context.getBean(Microsoft.class)).isNotNull();
        }

        System.clearProperty(Constants.APP_ID_PROPERTY);
        System.clearProperty(Constants.APP_SCERET_PROPERTY);
    }

    @Test
    public void cannotAutowire() {
        try (AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext()) {
            context.register(MicrosoftAutoConfiguration.class);
            context.register(SocialWebAutoConfiguration.class);
            context.refresh();

            Microsoft microsoft = null;
            try {
                microsoft = context.getBean(Microsoft.class);
            }
            catch (Exception e) {
                assertThat(e.getMessage()).contains("No qualifying bean of type 'com.microsoft.azure.msgraph.api.Microsoft' available");
            }
            assertThat(microsoft).isNull();
        }
    }
}
