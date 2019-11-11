/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.utils;

import com.microsoft.azure.management.appservice.PublishingProfile;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;
import java.io.InputStream;

public class ManagementUtils {

    /**
     * Uploads a file to an Azure web app.
     * @param profile the publishing profile for the web app.
     * @param fileName the name of the file on server
     * @param file the local file
     */
    public static void uploadFileToWebAppWwwRoot(PublishingProfile profile, String fileName, InputStream file) {
        final FTPClient ftpClient = new FTPClient();
        final String[] ftpUrlSegments = profile.ftpUrl().split("/", 2);
        final String server = ftpUrlSegments[0];
        String path = "./site/wwwroot";

        if (fileName.contains("/")) {
            final int lastSlash = fileName.lastIndexOf('/');
            path = path + "/" + fileName.substring(0, lastSlash);
            fileName = fileName.substring(lastSlash + 1);
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
            ftpClient.storeFile(fileName, file);
            ftpClient.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
