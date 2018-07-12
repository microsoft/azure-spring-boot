/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.aad;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.ClientCredential;
import com.microsoft.aad.adal4j.UserAssertion;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.naming.ServiceUnavailableException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class AzureADGraphClient {
    private static final SimpleGrantedAuthority DEFAULT_AUTHORITY = new SimpleGrantedAuthority("ROLE_USER");
    private static final String DEFAULE_ROLE_PREFIX = "ROLE_";
    private static final String REQUEST_ID_SUFFIX = "aadfeed5";

    private String clientId;
    private String clientSecret;
    private List<String> aadTargetGroups;
    private ServiceEndpoints serviceEndpoints;

    public AzureADGraphClient(ClientCredential clientCredential, AADAuthenticationProperties aadAuthProps,
                              ServiceEndpointsProperties serviceEndpointsProps) {
        this.clientId = clientCredential.getClientId();
        this.clientSecret = clientCredential.getClientSecret();
        this.aadTargetGroups = aadAuthProps.getActiveDirectoryGroups();
        this.serviceEndpoints = serviceEndpointsProps.getServiceEndpoints(aadAuthProps.getEnvironment());
    }

    private String getUserMembershipsV1(String accessToken) throws IOException {
        final URL url = new URL(serviceEndpoints.getAadMembershipRestUri());

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
            throw new IllegalStateException("Response is not " + HTTPResponse.SC_OK +
                    ", response json: " + responseInJson);
        }
    }

    private static String getResponseStringFromConn(HttpURLConnection conn) throws IOException {

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            final StringBuilder stringBuffer = new StringBuilder();
            String line = "";
            while ((line = reader.readLine()) != null) {
                stringBuffer.append(line);
            }
            return stringBuffer.toString();
        }
    }

    public List<UserGroup> getGroups(String graphApiToken) throws IOException {
        return loadUserGroups(graphApiToken);
    }

    private List<UserGroup> loadUserGroups(String graphApiToken) throws IOException {
        final String responseInJson = getUserMembershipsV1(graphApiToken);
        final List<UserGroup> lUserGroups = new ArrayList<>();
        final ObjectMapper objectMapper = JacksonObjectMapperFactory.getInstance();
        final JsonNode rootNode = objectMapper.readValue(responseInJson, JsonNode.class);
        final JsonNode valuesNode = rootNode.get("value");

        if (valuesNode != null) {
            valuesNode.forEach(valueNode -> {
                if (valueNode != null && valueNode.get("objectType").asText().equals("Group")) {
                    final String objectID = valueNode.get("objectId").asText();
                    final String displayName = valueNode.get("displayName").asText();
                    lUserGroups.add(new UserGroup(objectID, displayName));
                }
            });
        }

        return lUserGroups;
    }

    public Set<GrantedAuthority> getGrantedAuthorities(String graphApiToken) throws IOException {
        // Fetch the authority information from the protected resource using accessToken
        final List<UserGroup> groups = getGroups(graphApiToken);

        // Map the authority information to one or more GrantedAuthority's and add it to mappedAuthorities
        return convertGroupsToGrantedAuthorities(groups);
    }


    /**
     * Converts UserGroup list to Set of GrantedAutorities
     * @param groups
     * @return
     */
    public Set<GrantedAuthority> convertGroupsToGrantedAuthorities(final List<UserGroup> groups) {
        // Map the authority information to one or more GrantedAuthority's and add it to mappedAuthorities
        final Set<GrantedAuthority> mappedAuthorities = groups.stream()
                .filter(group -> aadTargetGroups.contains(group.getDisplayName()))
                .map(userGroup -> new SimpleGrantedAuthority(DEFAULE_ROLE_PREFIX + userGroup.getDisplayName()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (mappedAuthorities.isEmpty()) {
            mappedAuthorities.add(DEFAULT_AUTHORITY);
        }

        return mappedAuthorities;
    }

    public AuthenticationResult acquireTokenForGraphApi(String idToken, String tenantId)
            throws MalformedURLException, ServiceUnavailableException, InterruptedException, ExecutionException {

        final ClientCredential credential = new ClientCredential(clientId, clientSecret);
        final UserAssertion assertion = new UserAssertion(idToken);

        AuthenticationResult result = null;
        ExecutorService service = null;
        try {
            service = Executors.newFixedThreadPool(1);
            final AuthenticationContext context = new AuthenticationContext(
                    serviceEndpoints.getAadSigninUri() + tenantId + "/", true, service);
            context.setCorrelationId(getCorrelationId());
            final Future<AuthenticationResult> future = context
                    .acquireToken(serviceEndpoints.getAadGraphApiUri(), assertion, credential, null);
            result = future.get();
        } finally {
            if (service != null) {
                service.shutdown();
            }
        }

        if (result == null) {
            throw new ServiceUnavailableException(
                    "unable to acquire on-behalf-of token for client " + clientId);
        }
        return result;
    }

    private static String getCorrelationId() {
        final String uuid = UUID.randomUUID().toString();
        return uuid.substring(0, uuid.length() - REQUEST_ID_SUFFIX.length()) + REQUEST_ID_SUFFIX;
    }
}
