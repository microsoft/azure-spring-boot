/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.common;


import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class GetMacAddress {
    private static final String HASH_ALGORITHM = "SHA-256";

    public static String getMacHash() {
        final String macAddress = getRawMacAddress();

        if (macAddress == null || macAddress.isEmpty()) {
            return null;
        }
        try {
            final byte[] bytes = macAddress.getBytes("UTF-8");
            return hashRawBytes(bytes);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    private static String getRawMacAddress() {
        String ret = null;

        try {
            final String os = System.getProperty("os.name").toLowerCase();
            String[] command = {"ifconfig", "-a"};
            if (os != null && !os.isEmpty() && os.startsWith("win")) {
                command = new String[] {"getmac"};
            }

            final ProcessBuilder builder = new ProcessBuilder(command);
            final Process process = builder.start();
            final InputStream inputStream = process.getInputStream();
            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            final BufferedReader br = new BufferedReader(inputStreamReader);
            String tmp;
            while ((tmp = br.readLine()) != null) {
                ret += tmp;
            }

            if (inputStream != null) {
                inputStream.close();
            }
            if (br != null) {
                br.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return ret;
    }

    private static String hashRawBytes(byte[] mac) {
        if (mac.length == 0) {
            return null;
        }
        String ret = "";

        try {
            final MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            md.update(mac);

            final byte[] bytesAfterDigest = md.digest();
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytesAfterDigest.length; i++) {
                sb.append(Integer.toString((bytesAfterDigest[i] & 0xff) + 0x100, 16).substring(1));
            }
            ret = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return ret;
    }
}
