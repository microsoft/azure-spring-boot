/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.test;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.SocketUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AppRunner implements AutoCloseable {

    private Class<?> appClass;
    private Map<String, String> props;
    
    private ConfigurableApplicationContext app;
    
    public AppRunner(Class<?> appClass) {
        this.appClass = appClass;
        props = new LinkedHashMap<>();
    }
    
    public void property(String key, String value) {
        props.put(key, value);
    }
    
    public void start() {
        if (app == null) {
            SpringApplicationBuilder builder = new SpringApplicationBuilder(appClass);
            builder.properties("spring.jmx.enabled=false");
            builder.properties(String.format("server.port=%d", availableTcpPort()));
            builder.properties(props());
            
            app = builder.build().run();
        }
    }
    
    private int availableTcpPort() {
        return SocketUtils.findAvailableTcpPort();
    }
    
    private String [] props() {
        List<String> result = new ArrayList<>();
        
        for (String key : props.keySet()) {
            String value = props.get(key);
            result.add(String.format("%s=%s", key, value));
        }
        
        return result.toArray(new String [0]);
    }
    
    public void stop() {
        if (app != null) {
            app.close();
            app = null;
        }
    }
    
    public ConfigurableApplicationContext app() {
        return app;
    }
    
    public <T> T getBean(Class<T> type) {
        return app.getBean(type);
    }
    
    public ApplicationContext parent() {
        return app.getParent();
    }
    
    public <T> Map<String, T> getParentBeans(Class<T> type) {
        return parent().getBeansOfType(type);
    }

    public String getProperty(String key) {
        if (app == null) {
            throw new RuntimeException("App is not running.");
        }
        return app.getEnvironment().getProperty(key);
    }
    
    public int port() {
        if (app == null) {
            throw new RuntimeException("App is not running.");
        }
        return app.getEnvironment().getProperty("server.port", Integer.class, -1);
    }
    
    public String root() {
        if (app == null) {
            throw new RuntimeException("App is not running.");
        }
        
        String protocol = tlsEnabled() ? "https" : "http";
        return String.format("%s://localhost:%d/", protocol, port());
    }
    
    private boolean tlsEnabled() {
        return app.getEnvironment().getProperty("server.ssl.enabled", Boolean.class, false);
    }

    @Override
    public void close() {
        stop();
    }
}
