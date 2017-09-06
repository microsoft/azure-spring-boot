/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.autoconfigure.documentdb.sample;

import com.microsoft.azure.documentdb.Database;
import com.microsoft.azure.documentdb.DocumentClient;
import com.microsoft.azure.documentdb.DocumentClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DocumentdbSampleApplication implements CommandLineRunner {

    @Autowired
    private DocumentClient documentClient;

    public static void main(String[] args) {
        SpringApplication.run(DocumentdbSampleApplication.class, args);
    }

    public void run(String... var1) throws Exception {
        createDatabaseIfNotExists("testDB");
    }

    // Note: Here is the minimum sample code that demonstrates how DocumentClient is autowired and used.
    // For more complete Document DB sample code, please reference
    // https://github.com/Azure-Samples/azure-cosmos-db-documentdb-java-getting-started
    private void createDatabaseIfNotExists(String databaseName) throws DocumentClientException {
        final String databaseLink = String.format("/dbs/%s", databaseName);

        // Check to verify a database with the id=FamilyDB does not exist
        try {
            documentClient.readDatabase(databaseLink, null);
            System.out.println(String.format("Found %s", databaseName));
        } catch (DocumentClientException de) {
            // If the database does not exist, create a new database
            if (de.getStatusCode() == 404) {
                final Database database = new Database();
                database.setId(databaseName);

                documentClient.createDatabase(database, null);
                System.out.println(String.format("Created %s", databaseName));
            } else {
                throw de;
            }
        }
    }
}
