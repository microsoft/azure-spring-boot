/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.social.connect;

import com.microsoft.azure.spring.social.api.Microsoft;
import com.microsoft.azure.spring.social.api.MyProfile;
import org.springframework.social.ApiException;
import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.ConnectionValues;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.UserProfileBuilder;

public class MicrosoftAdapter implements ApiAdapter<Microsoft> {
    @Override
    public boolean test(Microsoft live) {
        try {
            live.meOperations().getMyProfile();
            return true;
        } catch (ApiException e) {
            return false;
        }
    }

    @Override
    public void setConnectionValues(Microsoft microsoft, ConnectionValues values) {
        final MyProfile profile = microsoft.meOperations().getMyProfile();
        values.setProviderUserId(profile.getId());
        values.setDisplayName(profile.getDisplayName());
    }

    @Override
    public UserProfile fetchUserProfile(Microsoft microsoft) {
        final MyProfile profile = microsoft.meOperations().getMyProfile();
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
