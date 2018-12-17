/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.aad;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

public class AADGraphHttpClientDefaultImplTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(9519);

    private AADGraphHttpClient aadGraphHttpClient;
    private String accessToken;

    @Before
    public void setUp() throws Exception {
        accessToken = "access";
        final ServiceEndpoints serviceEndpoints = new ServiceEndpoints();
        serviceEndpoints.setAadMembershipRestUri("http://localhost:9519/memberOf");
        aadGraphHttpClient = new AADGraphHttpClientDefaultImpl(serviceEndpoints);
    }

    @Test
    public void testMemberOf() throws Exception {
        stubFor(get(urlEqualTo("/memberOf"))
                .withHeader("Accept", equalTo("application/json;odata=minimalmetadata"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(Constants.USERGROUPS_JSON)));

        assertThat(aadGraphHttpClient.getMemberships(accessToken))
                .isEqualToIgnoringWhitespace(Constants.USERGROUPS_JSON);


        verify(getRequestedFor(urlMatching("/memberOf"))
                .withHeader("Authorization", equalTo(accessToken))
                .withHeader("Accept", equalTo("application/json;odata=minimalmetadata"))
                .withHeader("api-version", equalTo("1.6")));
    }

    @Test
    public void testMemberOfNot200Response() throws Exception {
        stubFor(get(urlEqualTo("/memberOf"))
                .withHeader("Accept", equalTo("application/json;odata=minimalmetadata"))
                .willReturn(aResponse()
                        .withStatus(202)
                        .withHeader("Content-Type", "application/json")
                        .withBody(Constants.USERGROUPS_JSON)));

        assertThatCode(() -> aadGraphHttpClient.getMemberships(accessToken))
                .isInstanceOf(AADGraphHttpClientException.class);


        verify(getRequestedFor(urlMatching("/memberOf"))
                .withHeader("Authorization", equalTo(accessToken))
                .withHeader("Accept", equalTo("application/json;odata=minimalmetadata"))
                .withHeader("api-version", equalTo("1.6")));
    }

    @Test
    public void testMemberOfErrorResponse() throws Exception {
        stubFor(get(urlEqualTo("/memberOf"))
                .withHeader("Accept", equalTo("application/json;odata=minimalmetadata"))
                .willReturn(aResponse()
                        .withStatus(501)
                        .withHeader("Content-Type", "application/json")
                        .withBody("Error")));

        assertThatCode(() -> aadGraphHttpClient.getMemberships(accessToken))
                .isInstanceOf(AADGraphHttpClientException.class)
                .hasMessage("Failed to read response from Membership call");


        verify(getRequestedFor(urlMatching("/memberOf"))
                .withHeader("Authorization", equalTo(accessToken))
                .withHeader("Accept", equalTo("application/json;odata=minimalmetadata"))
                .withHeader("api-version", equalTo("1.6")));
    }
}
