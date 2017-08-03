/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.autoconfigure.adintegration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AzureADUserProfile {

    private List<DirectoryServiceObject> UserMemberships;
    private static String userMembershipRestAPI = "https://graph.windows.net/me/memberOf?api-version=1.6";

    public AzureADUserProfile(String accessToken) {
        try {
            String responseInJson = getUserMembershipsV1(accessToken);
            UserMemberships = new ArrayList<DirectoryServiceObject>();
            ObjectMapper objectMapper = JacksonObjectMapperFactory.getInstance();
            JsonNode rootNode = objectMapper.readValue(responseInJson, JsonNode.class);
            JsonNode valuesNode = rootNode.get("value");
            int i = 0;
            while(valuesNode != null && valuesNode.get(i) != null) {
                UserMemberships.add(new DirectoryServiceObject(
                        valuesNode.get(i).get("odata.type").asText(),
                        valuesNode.get(i).get("objectType").asText(),
                        valuesNode.get(i).get("description").asText(),
                        valuesNode.get(i).get("displayName").asText()));
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<DirectoryServiceObject> getUserMemberships() {
        return UserMemberships;
    }

    private String getUserMembershipsV1(String accessToken) throws Exception {
        URL url = new URL(String.format(userMembershipRestAPI, accessToken));

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // Set the appropriate header fields in the request header.
        conn.setRequestProperty("api-version", "1.6");
        conn.setRequestProperty("Authorization", accessToken);
        conn.setRequestProperty("Accept", "application/json;odata=minimalmetadata");
        String responseInJson = getResponseStringFromConn(conn);
        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            return responseInJson;
        } else {
            throw new Exception(responseInJson);
        }
    }
    private String getResponseStringFromConn(HttpURLConnection conn) throws IOException {

        BufferedReader reader = null;
        reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuffer stringBuffer = new StringBuffer();
        String line = "";
        while ((line = reader.readLine()) != null) {
            stringBuffer.append(line);
        }
        return stringBuffer.toString();
    }
}
