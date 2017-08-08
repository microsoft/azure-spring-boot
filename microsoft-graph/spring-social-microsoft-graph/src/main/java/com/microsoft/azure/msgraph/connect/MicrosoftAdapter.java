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
        values.setProviderUserId(profile.id);
        values.setDisplayName(profile.displayName);

        // Not implemented yet.
        values.setImageUrl(null);
        values.setProfileUrl(null);
    }

    @Override
    public UserProfile fetchUserProfile(Microsoft microsoft) {
        final User profile = microsoft.userOperations().getUserProfile();
        return new UserProfileBuilder()
                .setId(profile.id)
                .setFirstName(profile.givenName)
                .setLastName(profile.surname)
                .setName(profile.displayName)
                .setEmail(profile.mail)
                .build();
    }

    @Override
    public void updateStatus(Microsoft microsoft, String message) {
        // not implemented yet.
        // microsoft.userOperations().updateStatus(message);
        throw new UnsupportedOperationException("MicrosoftAdapter:updateStatus not implemented yet.");
    }
}
