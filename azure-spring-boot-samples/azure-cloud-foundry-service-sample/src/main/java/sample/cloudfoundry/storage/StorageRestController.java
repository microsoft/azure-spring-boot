/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package sample.cloudfoundry.storage;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

@RestController
public class StorageRestController {

    public static final String IMAGE_PATH =
            "https://raw.githubusercontent.com/mjeffries-pivotal/pcf-samples/master/images/azure-pcf.jpg";
    private static final Logger LOG = LoggerFactory
            .getLogger(StorageRestController.class);
    @Autowired
    private CloudStorageAccount account;

    @RequestMapping(value = "/blob", method = RequestMethod.GET)
    @ResponseBody
    public void showBlob(HttpServletResponse response) {
        InputStream is = null;

        try {
            LOG.info("showBlob start");
            if (account == null) {
                LOG.error("Storage Account is null!");
                return;
            }

            final URL u = new URL(IMAGE_PATH);
            is = u.openStream();
            response.setContentType(MediaType.IMAGE_JPEG_VALUE);
            final int imageSize = IOUtils.copy(is, response.getOutputStream());

            LOG.debug("Connecting to storage account...");
            final CloudBlobClient serviceClient = account
                    .createCloudBlobClient();

            // Container name must be lower case.
            final CloudBlobContainer container = serviceClient
                    .getContainerReference("myimages");
            container.createIfNotExists();

            // Upload an image file.
            LOG.debug("Uploading image...");
            final CloudBlockBlob blob = container
                    .getBlockBlobReference("image1.jpg");
            blob.upload(new URL(IMAGE_PATH).openStream(), imageSize);
            LOG.debug("Uploading image complete");

        } catch (IOException e) {
            LOG.error("Error retrieving image", e);
        } catch (URISyntaxException | StorageException e) {
            LOG.error("Error accessing azure storage container", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    LOG.warn("Failed to close the InputStream.", e);
                }
            }
        }
    }
}
