/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.aad;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AzureADGraphClientTest {

    private AzureADGraphClient adGraphClient;
    @Mock
    private AADAuthenticationProperties aadAuthProps;

    @Mock
    private ServiceEndpointsProperties endpointsProps;

    @Mock
    private AADGraphHttpClient aadGraphHttpClient;

    @Before
    public void setup() throws Exception {
        final List<String> activeDirectoryGroups = new ArrayList<>();
        activeDirectoryGroups.add("Test_Group");
        when(aadAuthProps.getActiveDirectoryGroups()).thenReturn(activeDirectoryGroups);
        adGraphClient = new AzureADGraphClient("abc", "password", aadAuthProps, endpointsProps, aadGraphHttpClient);
    }

    @Test
    public void testConvetGroupToGrantedAuthorities() {

        final String groupName = "Test_Group";
        final UserGroup userGroup = new UserGroup("testId", groupName);
        final List<UserGroup> userGroups = new ArrayList<>();
        userGroups.add(userGroup);
        final Set<GrantedAuthority> authorities = adGraphClient.convertGroupsToGrantedAuthorities(userGroups);
        Assert.assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_" + groupName)));
    }
}
