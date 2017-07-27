/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.msgraph.connect;

import com.microsoft.azure.msgraph.api.Microsoft;
import com.microsoft.azure.msgraph.api.User;
import com.microsoft.azure.msgraph.api.UserOperations;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.social.connect.ConnectionValues;
import org.springframework.social.connect.UserProfile;

import static org.assertj.core.api.Assertions.assertThat;

public class MicrosoftAdapterTest {
    private MicrosoftAdapter apiAdapter = new MicrosoftAdapter();
    private Microsoft microsoft = Mockito.mock(Microsoft.class);

    @Test
    public void fetchProfile() {
        UserOperations userOperations = Mockito.mock(UserOperations.class);
        Mockito.when(microsoft.userOperations()).thenReturn(userOperations);

        User user = new User();
        user.setId("12345678");
        user.setDisplayName("Craig Walls");
        user.setSurname("Walls");
        user.setGivenName("Craig");
        user.setMail("zz@a.com");
        Mockito.when(userOperations.getUserProfile()).thenReturn(user);

        UserProfile profile = apiAdapter.fetchUserProfile(microsoft);
        assertThat(profile.getId()).isEqualTo("12345678");
        assertThat(profile.getName()).isEqualTo("Craig Walls");
        assertThat(profile.getFirstName()).isEqualTo("Craig");
        assertThat(profile.getLastName()).isEqualTo("Walls");
        assertThat(profile.getEmail()).isEqualTo("zz@a.com");
        assertThat(profile.getUsername()).isNull();
    }

    @Test
    public void setConnectionValues() throws Exception {
        UserOperations userOperations = Mockito.mock(UserOperations.class);
        Mockito.when(microsoft.userOperations()).thenReturn(userOperations);

        User user = new User();
        user.setId("12345678");
        user.setDisplayName("Craig Walls");
        user.setSurname("Walls");
        user.setGivenName("Craig");
        user.setMail("zz@a.com");
        Mockito.when(userOperations.getUserProfile()).thenReturn(user);

        TestConnectionValues connectionValues = new TestConnectionValues();
        apiAdapter.setConnectionValues(microsoft, connectionValues);
        assertThat(connectionValues.getDisplayName()).isEqualTo("Craig Walls");
        assertThat(connectionValues.getProviderUserId()).isEqualTo("12345678");
        assertThat(connectionValues.getImageUrl()).isNull();
        assertThat(connectionValues.getProfileUrl()).isNull();
    }

    private static class TestConnectionValues implements ConnectionValues {

        private String displayName;
        private String imageUrl;
        private String profileUrl;
        private String providerUserId;

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getProfileUrl() {
            return profileUrl;
        }

        public void setProfileUrl(String profileUrl) {
            this.profileUrl = profileUrl;
        }

        public String getProviderUserId() {
            return providerUserId;
        }

        public void setProviderUserId(String providerUserId) {
            this.providerUserId = providerUserId;
        }
    }
}
