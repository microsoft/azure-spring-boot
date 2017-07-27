/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.msgraph.api.impl;

import com.microsoft.azure.msgraph.api.MeOperations;
import com.microsoft.azure.msgraph.api.UserProfile;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

public class UserTemplate extends AbstractMicrosoftOperations implements MeOperations {

    private final RestTemplate restTemplate;

    public UserTemplate(RestTemplate restTemplate, boolean authorized) {
        super(authorized);
        this.restTemplate = restTemplate;
    }

    @Override
    public UserProfile getUserProfile() {
        final Map<String, ?> me = restTemplate.getForObject(buildUri("me"), Map.class);

        final UserProfile userProfile = new UserProfile();

        if (me.get("id") != null){
            userProfile.setId(String.valueOf(me.get("id")));
        }
        if (me.get("displayName") != null){
            userProfile.setDisplayName(String.valueOf(me.get("displayName")));
        }
        if (me.get("jobTitle") != null){
            userProfile.setJobTitle(String.valueOf(me.get("jobTitle")));
        }
        if (me.get("mail") != null){
            userProfile.setMail(String.valueOf(me.get("mail")));
        }
        if (me.get("surname") != null){
            userProfile.setSurname(String.valueOf(me.get("surname")));
        }
        if (me.get("givenName") != null){
            userProfile.setGivenName(String.valueOf(me.get("givenName")));
        }
        if (me.get("mobilePhone") != null){
            userProfile.setMobilePhone(String.valueOf(me.get("mobilePhone")));
        }
        if (me.get("officeLocation") != null){
            userProfile.setOfficeLocation(String.valueOf(me.get("officeLocation")));
        }
        if (me.get("preferredLanguage") != null){
            userProfile.setPreferredLanguage(String.valueOf(me.get("preferredLanguage")));
        }
        if (me.get("userPrincipalName") != null){
            userProfile.setUserPrincipalName(String.valueOf(me.get("userPrincipalName")));
        }

        final List<String> businessPhones = (List<String>) me.get("businessPhones");
        if (businessPhones != null){
            userProfile.setBusinessPhones(businessPhones);
        }

        return userProfile;
    }
}
