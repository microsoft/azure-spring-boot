/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.utils;

import com.microsoft.azure.management.appservice.PublishingProfile;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

@Slf4j
public class ManagementUtils {

    /**
     * Uploads a file to an Azure web app.
     * @param profile the publishing profile for the web app.
     * @param fileNameOrPath the name of the file on server
     * @param file the local file
     */
    public static void uploadFileToWebAppWwwRoot(PublishingProfile profile, String fileNameOrPath, InputStream file) {
        final FTPClient ftpClient = new FTPClient();
        final String[] ftpUrlSegments = profile.ftpUrl().split("/", 2);

        log.info("uploading to ftp " + Arrays.toString(ftpUrlSegments));
        final String server = ftpUrlSegments[0];
        String path = "./site/wwwroot";
        String fileName = fileNameOrPath;

        if (fileNameOrPath.contains("/")) {
            final int lastSlash = fileNameOrPath.lastIndexOf('/');
            path = path + "/" + fileNameOrPath.substring(0, lastSlash);
            fileName = fileNameOrPath.substring(lastSlash + 1);
        }
        try {
            ftpClient.connect(server);
            ftpClient.login(profile.ftpUsername(), profile.ftpPassword());
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            for (final String segment : path.split("/")) {
                if (!ftpClient.changeWorkingDirectory(segment)) {
                    ftpClient.makeDirectory(segment);
                    ftpClient.changeWorkingDirectory(segment);
                }
            }

            boolean result = ftpClient.storeFile(fileName, file);
            log.info("store file result: " + result);
            FTPFile[] ftpFiles = ftpClient.listDirectories();
            for (FTPFile ftpFile : ftpFiles) {
                log.info("listing ftpfile " + ftpFile.getName());
            }
            ftpClient.disconnect();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

}
