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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.naming.ServiceUnavailableException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Slf4j
public class AzureADGraphClient {
    private static final SimpleGrantedAuthority DEFAULT_AUTHORITY = new SimpleGrantedAuthority("ROLE_USER");
    private static final String DEFAULE_ROLE_PREFIX = "ROLE_";
    private static final String REQUEST_ID_SUFFIX = "aadfeed5";

    private final String clientId;
    private final String clientSecret;
    private final List<String> aadTargetGroups;
    private final ServiceEndpoints serviceEndpoints;
    private final AADGraphHttpClient aadGraphHttpClient;

    public AzureADGraphClient(String clientId, String clientSecret, AADAuthenticationProperties aadAuthProps,
                              ServiceEndpointsProperties serviceEndpointsProps, AADGraphHttpClient aadGraphHttpClient) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.aadTargetGroups = aadAuthProps.getActiveDirectoryGroups();
        this.aadGraphHttpClient = aadGraphHttpClient;
        this.serviceEndpoints = serviceEndpointsProps.getServiceEndpoints(aadAuthProps.getEnvironment());
    }


    public List<UserGroup> getGroups(String graphApiToken) throws IOException, AADGraphHttpClientException {
        return loadUserGroups(graphApiToken);
    }

    private List<UserGroup> loadUserGroups(String graphApiToken) throws IOException, AADGraphHttpClientException {
        final String responseInJson = aadGraphHttpClient.getMemberships(graphApiToken);
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

        log.debug("Extracted UserGroups - {}", lUserGroups);
        return lUserGroups;
    }

    public Set<GrantedAuthority> getGrantedAuthorities(String graphApiToken)
            throws IOException, AADGraphHttpClientException {
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

        final AuthenticationResult result;
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
