package com.microsoft.azure.spring.autoconfigure.aad;


/**
 * Defines a class used to make HTTP calls within the Azure Spring Boot Starter.
 */
public interface AADGraphHttpClient {

    /**
     * Gets memberships for the current user calling to the {@link ServiceEndpoints#getAadMembershipRestUri()}.
     *
     * @param accessToken - accessToken to call to the REST uri to retrieve the groups the user is a member of.
     * @return JSON string of the AD group memberships.
     * @throws AADGraphHttpClientException - If any HTTP returns anything but a 2xx status code.
     */
    String getMemberships(String accessToken) throws AADGraphHttpClientException;
}
