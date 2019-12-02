/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.test.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

public class OAuthUtils {

    public static final String AAD_CLIENT_ID = "AAD_CLIENT_ID";
    public static final String AAD_CLIENT_SECRET = "AAD_CLIENT_SECRET";
    private static final String AAD_TENANT_ID = "AAD_TENANT_ID";
    private static final String AAD_USER_NAME = "AAD_USER_NAME";
    private static final String AAD_USER_PASSWORD = "AAD_USER_PASSWORD";

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final OkHttpClient client = new OkHttpClient();

    public static OAuthResponse executeOAuth2ROPCFlow() {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, String.format("client_id=%s" +
                        "&scope=user.read openid profile offline_access" +
                        "&client_secret=%s" +
                        "&username=%s" +
                        "&password=%s" +
                        "&grant_type=password",
                System.getenv(AAD_CLIENT_ID),
                System.getenv(AAD_CLIENT_SECRET),
                System.getenv(AAD_USER_NAME),
                System.getenv(AAD_USER_PASSWORD)
        ));

        Request request = new Request.Builder()
                .url(String.format("https://login.microsoftonline.com/%s/oauth2/v2.0/token",
                        System.getenv().get(AAD_TENANT_ID)))
                .post(body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Accept", "*/*")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Host", "login.microsoftonline.com")
                .addHeader("Accept-Encoding", "gzip, deflate")
                .addHeader("Content-Length", "237")
                .addHeader("Connection", "keep-alive")
                .addHeader("cache-control", "no-cache")
                .build();

        try {
            Response response = client.newCall(request).execute();

            assertNotNull("OAuth response body should not be null.", response.body());

            return objectMapper.readValue(response.body().string(), OAuthResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
