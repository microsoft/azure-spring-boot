/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.test;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Application implements CommandLineRunner {

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

    public void run(String... varl) throws Exception {
        System.out.println("property your-property-name value is: " + cosmosDBkey);
    }

}
