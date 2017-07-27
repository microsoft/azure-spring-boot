/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.msgraph.connect;

import com.microsoft.azure.msgraph.api.Microsoft;
import com.microsoft.azure.msgraph.api.User;
import org.springframework.social.ApiException;
import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.ConnectionValues;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.UserProfileBuilder;

public class MicrosoftAdapter implements ApiAdapter<Microsoft> {
    @Override
    public boolean test(Microsoft microsoft) {
        try {
            microsoft.userOperations().getUserProfile();
            return true;
        } catch (ApiException e) {
            return false;
        }
    }

    @Override
    public void setConnectionValues(Microsoft microsoft, ConnectionValues values) {
        final User profile = microsoft.userOperations().getUserProfile();
        values.setProviderUserId(profile.getId());
        values.setDisplayName(profile.getDisplayName());

        // Not implemented yet.
        values.setImageUrl(null);
        values.setProfileUrl(null);
    }

    @Override
    public UserProfile fetchUserProfile(Microsoft microsoft) {
        final User profile = microsoft.userOperations().getUserProfile();
        return new UserProfileBuilder()
                .setId(profile.getId())
                .setFirstName(profile.getGivenName())
                .setLastName(profile.getSurname())
                .setName(profile.getDisplayName())
                .setEmail(profile.getMail())
                .build();
    }

    @Override
    public void updateStatus(Microsoft microsoft, String message) {
        // not implemented yet.
        // microsoft.userOperations().updateStatus(message);
    }
}
