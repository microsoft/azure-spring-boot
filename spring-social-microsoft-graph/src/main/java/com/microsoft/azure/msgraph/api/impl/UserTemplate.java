/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.msgraph.api.impl;

import com.microsoft.azure.msgraph.api.UserOperations;
import com.microsoft.azure.msgraph.api.User;

import java.util.List;
import java.util.Map;

public class UserTemplate extends AbstractMicrosoftOperations implements UserOperations {
    private final MicrosoftTemplate microsoft;

    public UserTemplate(MicrosoftTemplate microsoft, boolean authorized) {
        super(authorized);
        this.microsoft = microsoft;
    }

    @Override
    public User getUserProfile() {
        final Map<String, ?> me = microsoft.fetchObject("me", Map.class);

        final User user = new User();

        if (me.get("id") != null) {
            user.setId(String.valueOf(me.get("id")));
        }
        if (me.get("displayName") != null) {
            user.setDisplayName(String.valueOf(me.get("displayName")));
        }
        if (me.get("jobTitle") != null) {
            user.setJobTitle(String.valueOf(me.get("jobTitle")));
        }
        if (me.get("mail") != null) {
            user.setMail(String.valueOf(me.get("mail")));
        }
        if (me.get("surname") != null) {
            user.setSurname(String.valueOf(me.get("surname")));
        }
        if (me.get("givenName") != null) {
            user.setGivenName(String.valueOf(me.get("givenName")));
        }
        if (me.get("mobilePhone") != null) {
            user.setMobilePhone(String.valueOf(me.get("mobilePhone")));
        }
        if (me.get("officeLocation") != null) {
            user.setOfficeLocation(String.valueOf(me.get("officeLocation")));
        }
        if (me.get("preferredLanguage") != null) {
            user.setPreferredLanguage(String.valueOf(me.get("preferredLanguage")));
        }
        if (me.get("userPrincipalName") != null) {
            user.setUserPrincipalName(String.valueOf(me.get("userPrincipalName")));
        }

        final List<String> businessPhones = (List<String>) me.get("businessPhones");
        if (businessPhones != null) {
            user.setBusinessPhones(businessPhones);
        }

        return user;
    }
}
