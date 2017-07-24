/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.common;


import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class GetMacAddress {
    private static final String HASH_ALGORITHM = "SHA-256";

    public static String getMacHash() {
        final byte[] macAddress = getRawMacAddress();

        return hashRawBytes(macAddress);
    }

    private static byte[] getRawMacAddress() {

        try {
            final InetAddress ip = InetAddress.getLocalHost();
            final NetworkInterface network = NetworkInterface.getByInetAddress(ip);

            return network.getHardwareAddress();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return new byte[0];
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
