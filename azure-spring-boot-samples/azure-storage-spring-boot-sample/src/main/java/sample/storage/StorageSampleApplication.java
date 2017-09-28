/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package sample.storage;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.URISyntaxException;

@SpringBootApplication
public class StorageSampleApplication implements CommandLineRunner {
    @Autowired
    private CloudStorageAccount cloudStorageAccount;

    public static void main(String[] args) {
        SpringApplication.run(StorageSampleApplication.class);
    }

    public void run(String... var1) throws URISyntaxException, StorageException {
        createContainerIfNotExists("mycontainer");
    }

    // Note: Here is the minimum sample code that demonstrates how CloudStorageAccount is autowired and used.
    // For more complete Azure Storage API usage, please go to https://github.com/Azure-Samples and search repositories
    // with key words `storage` and 'java'.
    private void createContainerIfNotExists(String containerName) throws URISyntaxException, StorageException {
        // Create the blob client.
        final CloudBlobClient blobClient = cloudStorageAccount.createCloudBlobClient();

        // Get a reference to a container.
        // The container name must be lower case
        final CloudBlobContainer container = blobClient.getContainerReference(containerName);

        // Create the container if it does not exist.
        container.createIfNotExists();
    }
}
