/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package sample.keyvault;

import com.microsoft.azure.keyvault.spring.KeyVaultOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SampleApplication implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(SampleApplication.class);

    @Value("${yourSecretPropertyName}")
    private String mySecretProperty;

    @Autowired
    private KeyVaultOperation keyVaultOperation;

    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }

    public void run(String... varl) throws Exception {
        System.out.println("property yourSecretPropertyName in Azure Key Vault: " + mySecretProperty);
        final Object grabProperty = keyVaultOperation.get("yourSecretPropertyName");
        System.out.println("property yourSecretPropertyName in Azure Key Vault queried with operation: " + grabProperty);
    }

}
