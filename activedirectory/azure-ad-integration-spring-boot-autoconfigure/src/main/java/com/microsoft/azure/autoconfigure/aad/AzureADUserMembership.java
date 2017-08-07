/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.autoconfigure.aad;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class AzureADUserMembership {

    private List<DirectoryServiceObject> userMemberships;
    private static String userMembershipRestAPI = "https://graph.windows.net/me/memberOf?api-version=1.6";

    public AzureADUserMembership(String accessToken) throws Exception {
        final String responseInJson = getUserMembershipsV1(accessToken);
        userMemberships = new ArrayList<DirectoryServiceObject>();
        final ObjectMapper objectMapper = JacksonObjectMapperFactory.getInstance();
        final JsonNode rootNode = objectMapper.readValue(responseInJson, JsonNode.class);
        final JsonNode valuesNode = rootNode.get("value");
        int i = 0;
        while (valuesNode != null && valuesNode.get(i) != null) {
            userMemberships.add(new DirectoryServiceObject(
                    valuesNode.get(i).get("odata.type").asText(),
                    valuesNode.get(i).get("objectType").asText(),
                    valuesNode.get(i).get("description").asText(),
                    valuesNode.get(i).get("displayName").asText()));
            i++;
        }
    }

    public List<DirectoryServiceObject> getUserMemberships() {
        return userMemberships;
    }

    private String getUserMembershipsV1(String accessToken) throws Exception {
        final URL url = new URL(userMembershipRestAPI);

        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // Set the appropriate header fields in the request header.
        conn.setRequestProperty("api-version", "1.6");
        conn.setRequestProperty("Authorization", accessToken);
        conn.setRequestProperty("Accept", "application/json;odata=minimalmetadata");
        final String responseInJson = getResponseStringFromConn(conn);
        final int responseCode = conn.getResponseCode();
        if (responseCode == HTTPResponse.SC_OK) {
            return responseInJson;
        } else {
            throw new Exception(responseInJson);
        }
    }
    private String getResponseStringFromConn(HttpURLConnection conn) throws IOException {

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            final StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null) {
                stringBuffer.append(line);
            }
            return stringBuffer.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
}
