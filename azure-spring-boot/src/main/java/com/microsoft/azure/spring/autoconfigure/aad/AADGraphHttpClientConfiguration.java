package com.microsoft.azure.spring.autoconfigure.aad;

import com.microsoft.aad.adal4j.ClientCredential;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class AADGraphHttpClientConfiguration {


    @Bean
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    @ConditionalOnMissingBean(AADGraphHttpClient.class)
    public AADGraphHttpClient aadHttpClient(ServiceEndpointsProperties serviceEndpointsProps,
                                            AADAuthenticationProperties aadAuthProps) {
        return new AADGraphHttpClientDefaultImpl(serviceEndpointsProps.getServiceEndpoints(
                aadAuthProps.getEnvironment()));
    }


    @Bean
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    @ConditionalOnMissingBean
    public AzureADGraphClient azureADGraphClient(AADGraphHttpClient aaaHttpClient,
                                                 AADAuthenticationProperties aadAuthProps,
                                                 ServiceEndpointsProperties serviceEndpointsProps) {
        return new AzureADGraphClient(new ClientCredential(aadAuthProps.getClientId(), aadAuthProps.getClientSecret()),
                aadAuthProps, serviceEndpointsProps, aaaHttpClient);
    }

}
