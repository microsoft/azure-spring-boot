/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.aad;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

/**
 * Implementation that delegates to a {@link RestTemplate}
 * for the REST calls. The Constructor accepts a {@link RestTemplateBuilder} which allows customization
 * prior to injection.
 */
@Slf4j
public class AADGraphHTTPClientRestTemplateImplGraph implements AADGraphHttpClient {


    private final ServiceEndpoints serviceEndpoints;
    private final RestTemplate restTemplate;

    public AADGraphHTTPClientRestTemplateImplGraph
            (ServiceEndpoints serviceEndpoints,
             RestTemplateBuilder builder) {
        Assert.notNull(builder, "No RestTemplateBuilder Supplied.");
        this.serviceEndpoints = serviceEndpoints;
        this.restTemplate = builder.build();
    }


    @Override
    public String getMemberships(String accessToken) throws AADGraphHttpClientException {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("api-version", "1.6");
        httpHeaders.set("Authorization", "Bearer " + accessToken);
        httpHeaders.set("Accept", "application/json;odata=minimalmetadata");

        final HttpEntity httpEntity = new HttpEntity(httpHeaders);

        final ResponseEntity<String> response = restTemplate.exchange(serviceEndpoints.getAadMembershipRestUri(),
                HttpMethod.GET, httpEntity, String.class);

        final HttpStatus statusCode = response.getStatusCode();
        if (!statusCode.is2xxSuccessful()) {
            log.error("Response code was not 200. Status Code - {} Headers - {} Response - {}", statusCode
                    , response.getHeaders(), response.getHeaders());
            throw new AADGraphHttpClientException(String.format("Failed to get Membership for User. " +
                    "Response Status Code - %s", statusCode));
        } else {
            return response.getBody();
        }
    }
}
