/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package sample.storage;

import com.microsoft.azure.spring.autoconfigure.storage.StorageProperties;
import com.microsoft.azure.storage.blob.BlockBlobURL;
import com.microsoft.azure.storage.blob.ContainerURL;
import com.microsoft.azure.storage.blob.ServiceURL;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;

@SpringBootApplication
public class StorageSampleApplication implements CommandLineRunner {
    private static final String SOURCE_FILE = "storageTestFile.txt";

    @Autowired
    private ServiceURL serviceURL;

    @Autowired
    private ContainerURL containerURL;

    @Autowired
    private StorageProperties properties;

    public static void main(String[] args) {
        SpringApplication.run(StorageSampleApplication.class);
    }

    public void run(String... var1) throws IOException {
        File sourceFile = new File(this.getClass().getClassLoader().getResource(SOURCE_FILE).getFile());
        File downloadFile = Files.createTempFile("azure-storage-test", null).toFile();

        StorageService.createContainer(containerURL,  properties.getContainerName());
        BlockBlobURL blockBlobURL = containerURL.createBlockBlobURL(SOURCE_FILE);

        System.out.println("Enter a command:");
        System.out.println("(P)utBlob | (G)etBlob | (D)eleteBlobs | (E)xitSample");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while(true) {
            System.out.println("Enter a command:");
            String input = reader.readLine();

            switch(input) {
                case "P":
                    StorageService.uploadFile(blockBlobURL, sourceFile);
                    break;
                case "G":
                    StorageService.downloadBlob(blockBlobURL, downloadFile);
                    break;
                case "D":
                    StorageService.deleteBlob(blockBlobURL);
                    break;
                case "E":
                    System.out.println("Cleaning up container and tmp file...");
                    containerURL.delete(null, null).blockingGet();
                    FileUtils.deleteQuietly(downloadFile);
                    System.exit(0);
                default:
                    break;
            }
        }
    }
}
