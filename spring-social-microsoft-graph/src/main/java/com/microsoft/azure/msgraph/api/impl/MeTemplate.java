/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.msgraph.api.impl;

import com.microsoft.azure.msgraph.api.MeOperations;
import com.microsoft.azure.msgraph.api.MyProfile;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class MeTemplate extends AbstractMicrosoftOperations implements MeOperations {

    private final RestTemplate restTemplate;

    public MeTemplate(RestTemplate restTemplate, boolean authorized) {
        super(authorized);
        this.restTemplate = restTemplate;
    }

    @Override
    public MyProfile getMyProfile() {
        final Map<String, ?> me = restTemplate.getForObject(buildUri("me"), Map.class);

        final MyProfile myProfile = new MyProfile();
        myProfile.setId(String.valueOf(me.get("id")));
        myProfile.setDisplayName(String.valueOf(me.get("displayName")));
        myProfile.setJobTitle(String.valueOf(me.get("jobTitle")));
        myProfile.setMail(String.valueOf(me.get("mail")));

        return myProfile;
    }
}
