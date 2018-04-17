/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.keyvault.spring;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.microsoft.azure.keyvault.authentication.KeyVaultCredentials;
import lombok.Getter;
import lombok.Setter;
import okhttp3.*;

import java.io.IOException;

public class KeyVaultMsiCredentials extends KeyVaultCredentials{

    private static final String URL_BASE = "http://localhost:";
    private static final String URL_EXTENSION = "/oauth2/token";
    private static final Integer DEFAULT_PORT = 50342;
    private final Integer URL_PORT;

    public KeyVaultMsiCredentials(Integer port){
        this.URL_PORT = port;
    }
    public KeyVaultMsiCredentials(){
        this(DEFAULT_PORT);
    }

    @Override
    public String doAuthenticate(String authorization, String resource, String scope) {
        return getMsiToken(resource);
    }

    private String getMsiToken(String resource) {
        final OkHttpClient client = new OkHttpClient();
        final RequestBody formBody = new FormBody.Builder()
                .add("resource", resource)
                .build();
        final Request request = new Request.Builder()
                .url(URL_BASE + Integer.toString(URL_PORT) + URL_EXTENSION)
                .post(formBody)
                .addHeader("metadata", "true")
                .addHeader("cache-control", "no-cache")
                .build();
        try {
            final Response response = client.newCall(request).execute();
            final Gson gson = new Gson();
            final MSIResult msiResult = gson.fromJson(response.body().string(), MSIResult.class);
            return msiResult.getAccessToken();
        } catch (IOException | JsonSyntaxException e){
            throw new RuntimeException("Unable to get Token from MSI: " + e.getMessage());
        }
    }

    public class MSIResult {
        @Expose @Getter @Setter @SerializedName("access_token")
        private String accessToken;
        @Expose @Getter @Setter @SerializedName("expires_in")
        private String expiresIn;
        @Expose @Getter @Setter @SerializedName("expires_on")
        private String expiresOn;
        @Expose @Getter @Setter @SerializedName("not_before")
        private String notBefore;
        @Expose @Getter @Setter @SerializedName("resource")
        private String resource;
        @Expose @Getter @Setter @SerializedName("token_type")
        private String tokenType;
    }
}
