/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.test;

import com.alibaba.fastjson.JSON;
import com.azure.core.credential.TokenCredential;
import com.azure.identity.ClientCertificateCredentialBuilder;
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.identity.ManagedIdentityCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.keyvault.spring.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
@SpringBootApplication
@RestController
public class Application implements CommandLineRunner {

    @Autowired
    private ConfigurableEnvironment environment;


    @Value("${azure.cosmosdb.key:local}")
    private String cosmosDBkey;

    private static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @GetMapping("hello")
    public String hello() {
        try {
            return mapper.writeValueAsString(System.getenv());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "Some error happens";
        }
    }

    @GetMapping("get")
    public String get() {
        return cosmosDBkey;
    }

    @GetMapping("property/{key}")
    public String property(@PathVariable String key) {
        final String property = environment.getProperty(key);
        return property;
    }


    @GetMapping("list")
    public String list() {
        final List list = new ArrayList();
        final MutablePropertySources propertySources = this.environment.getPropertySources();
        final Iterator<PropertySource<?>> iterator = propertySources.iterator();
        while (iterator.hasNext()) {
            final PropertySource<?> next = iterator.next();
            list.add(next.getName());
        }
        return JSON.toJSONString(list);
    }

    @GetMapping("getPS/{ps}/{key}")
    public String getPS(@PathVariable String ps, @PathVariable String key) {
        final MutablePropertySources propertySources = this.environment.getPropertySources();
        final PropertySource<?> propertySource = propertySources.get(ps);
        if (propertySource != null) {
            final Object property = propertySource.getProperty(key);
            return property == null ? null : property.toString();
        } else {
            return null;
        }
    }

    @GetMapping("instanceget/{key}")
    public String vaultclient(@PathVariable String key) {
        final TokenCredential tokenCredential = getCredentials();
        log.info("----------------> credential is:{}", tokenCredential.getClass());
        final String vaultUri = getProperty(this.environment, Constants.AZURE_KEYVAULT_VAULT_URI);
        log.info("----------------> vault uri is:{}", vaultUri);
        final SecretClient secretClient = new SecretClientBuilder()
                .vaultUrl(vaultUri)
                .credential(tokenCredential)
                .buildClient();

        final KeyVaultSecret secret = secretClient.getSecret(key);
        final String value = secret.getValue();
        return value;
    }

    TokenCredential getCredentials() {
        log.info("using the new sdk......");
        //use service principle to authenticate
        if (this.environment.containsProperty(Constants.AZURE_KEYVAULT_CLIENT_ID)
                && this.environment.containsProperty(Constants.AZURE_KEYVAULT_CLIENT_KEY)
                && this.environment.containsProperty(Constants.AZURE_KEYVAULT_TENANT_ID)) {
            log.info("Will use service principle");
            final String clientId = getProperty(this.environment, Constants.AZURE_KEYVAULT_CLIENT_ID);
            final String clientKey = getProperty(this.environment, Constants.AZURE_KEYVAULT_CLIENT_KEY);
            final String tenantId = getProperty(this.environment, Constants.AZURE_KEYVAULT_TENANT_ID);
            final ClientSecretCredential clientSecretCredential = new ClientSecretCredentialBuilder()
                    .clientId(clientId)
                    .clientSecret(clientKey)
                    .tenantId(tenantId)
                    .build();
            return clientSecretCredential;
        }
        //use certificate to authenticate
        if (this.environment.containsProperty(Constants.AZURE_KEYVAULT_CLIENT_ID)
                && this.environment.containsProperty(Constants.AZURE_KEYVAULT_CERTIFICATE_PATH)
                && this.environment.containsProperty(Constants.AZURE_KEYVAULT_TENANT_ID)) {
            // Password can be empty
            final String certPwd = this.environment.getProperty(Constants.AZURE_KEYVAULT_CERTIFICATE_PASSWORD);
            final String certPath = getProperty(this.environment, Constants.AZURE_KEYVAULT_CERTIFICATE_PATH);
            log.info("Read certificate from {}...", certPath);

            if (StringUtils.isEmpty(certPwd)) {
                return new ClientCertificateCredentialBuilder()
                        .tenantId(getProperty(this.environment, Constants.AZURE_KEYVAULT_TENANT_ID))
                        .clientId(getProperty(this.environment, Constants.AZURE_KEYVAULT_CLIENT_ID))
                        .pemCertificate(certPath)
                        .build();
            } else {
                return new ClientCertificateCredentialBuilder()
                        .tenantId(getProperty(this.environment, Constants.AZURE_KEYVAULT_TENANT_ID))
                        .clientId(getProperty(this.environment, Constants.AZURE_KEYVAULT_CLIENT_ID))
                        .pfxCertificate(certPath, certPwd)
                        .build();
            }
        }
        //use MSI to authenticate
        if (this.environment.containsProperty(Constants.AZURE_KEYVAULT_CLIENT_ID)) {
            log.info("Will use MSI credentials with specified clientId");
            final String clientId = getProperty(this.environment, Constants.AZURE_KEYVAULT_CLIENT_ID);
            return new ManagedIdentityCredentialBuilder().clientId(clientId).build();
        }
        log.info("Will use MSI credentials");
        return new ManagedIdentityCredentialBuilder().build();
    }

    private String getProperty(final ConfigurableEnvironment env, final String propertyName) {
        Assert.notNull(env, "env must not be null!");
        Assert.notNull(propertyName, "propertyName must not be null!");
        final String property = env.getProperty(propertyName);
        if (property == null || property.isEmpty()) {
            throw new IllegalArgumentException("property " + propertyName + " must not be null");
        }
        return property;
    }


    public void run(String... varl) throws Exception {
        System.out.println("property your-property-name value is: " + cosmosDBkey);
    }

}
