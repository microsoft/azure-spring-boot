/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package sample.keyvault;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class SampleApplication implements CommandLineRunner {

    @Value("${yourSecretPropertyName9}")
    private String yourSecretPropertyName9;

    @Value("${property.in.raw}")
    private String propertyInRaw;

    @Value("${property.refer.to.another.property}")
    private String propertyReferToAnotherProperty;

    @Value("${property.refer.to.another.property.another}")
    private String propertyReferToAnotherPropertyAnother;

    @Value("${key-vault-value-contain-spel}")
    private String keyVaultValueContainSpel;

    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }

    public void run(String[] args) {
        log.info("yourSecretPropertyName9 = {}", yourSecretPropertyName9);
        log.info("propertyInRaw = {}", propertyInRaw);
        log.info("propertyReferToAnotherProperty = {}", propertyReferToAnotherProperty);
        log.info("propertyReferToAnotherPropertyAnother = {}", propertyReferToAnotherPropertyAnother);
        log.info("keyVaultKeyContainSpel = {}", keyVaultValueContainSpel);
    }

}
