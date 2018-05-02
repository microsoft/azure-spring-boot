/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.aad;

import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

/**
 * Yaml file initializer to load the specified yaml configuration file,
 * by default the Spring will load the application.yml file.
 * <p>
 * In order to avoid possible overwritten by users' default yaml configuration file.
 */
public class YamlFileApplicationContextInitializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final String SERVICE_ENDPOINTS_YAML = "classpath:serviceEndpoints.yml";

    private List<PropertySource<?>> yamlPropertySourceLoad(ConfigurableApplicationContext context) {
        final List<PropertySource<?>> serviceEndpoints;
        final Resource resource = context.getResource(SERVICE_ENDPOINTS_YAML);
        final YamlPropertySourceLoader sourceLoader = new YamlPropertySourceLoader();

        try {
            serviceEndpoints = sourceLoader.load("serviceEndpoints", resource);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot load the azure service endpoints configuration", e);
        }

        if (serviceEndpoints.size() != 1) {
            throw new IllegalStateException("There must be only 1 azure service endpoints configuration in classpath");
        }

        return serviceEndpoints;
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        final List<PropertySource<?>> serviceEndpoints = yamlPropertySourceLoad(applicationContext);

        applicationContext.getEnvironment().getPropertySources().addFirst(serviceEndpoints.get(0));
    }
}

