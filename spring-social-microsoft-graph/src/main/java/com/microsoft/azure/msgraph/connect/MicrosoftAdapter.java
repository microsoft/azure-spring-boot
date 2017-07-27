/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.msgraph.connect;

import com.microsoft.azure.msgraph.api.Microsoft;
import com.microsoft.azure.msgraph.api.UserProfile;
import org.springframework.social.ApiException;
import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.ConnectionValues;
import org.springframework.social.connect.UserProfileBuilder;

public class MicrosoftAdapter implements ApiAdapter<Microsoft> {
    @Override
    public boolean test(Microsoft live) {
        try {
            live.meOperations().getUserProfile();
            return true;
        } catch (ApiException e) {
            return false;
        }
    }

    @Override
    public void setConnectionValues(Microsoft microsoft, ConnectionValues values) {
        final UserProfile profile = microsoft.meOperations().getUserProfile();
        values.setProviderUserId(profile.getId());
        values.setDisplayName(profile.getDisplayName());
    }

    @Override
    public org.springframework.social.connect.UserProfile fetchUserProfile(Microsoft microsoft) {
        final UserProfile profile = microsoft.meOperations().getUserProfile();
        return new UserProfileBuilder()
                .setName(profile.getDisplayName())
                .setEmail(profile.getMail())
                .build();
    }

    @Override
    public void updateStatus(Microsoft microsoft, String message) {
        // not yet implemented
    }
}
