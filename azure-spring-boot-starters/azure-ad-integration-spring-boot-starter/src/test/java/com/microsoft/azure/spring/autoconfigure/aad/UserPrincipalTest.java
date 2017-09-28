/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.aad;

import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AzureADGraphClient.class)
public class UserPrincipalTest {
    @Test
    public void getAuthoritiesByUserGroups() throws Exception {
        final UserPrincipal principal = new UserPrincipal();
        final List<UserGroup> userGroups = new ArrayList<UserGroup>();
        userGroups.add(new UserGroup("this is group1", "group1"));

        final Collection<? extends GrantedAuthority> authorities =
                principal.getAuthoritiesByUserGroups(userGroups, Constants.TARGETED_GROUPS);
        Assert.assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_group1")));
        Assert.assertFalse(authorities.contains(new SimpleGrantedAuthority("ROLE_group2")));
        Assert.assertFalse(authorities.contains(new SimpleGrantedAuthority("ROLE_group3")));
    }

    @Test
    public void getGroups() throws Exception {
        PowerMockito.mockStatic(AzureADGraphClient.class);
        Mockito.when(AzureADGraphClient.getUserMembershipsV1(Constants.BEARER_TOKEN))
                .thenReturn(Constants.USERGROUPS_JSON);

        final UserPrincipal principal = new UserPrincipal();

        final List<UserGroup> groups = principal.getGroups(Constants.BEARER_TOKEN);
        final List<UserGroup> targetedGroups = new ArrayList<UserGroup>();
        targetedGroups.add(new UserGroup("12345678-7baf-48ce-96f4-a2d60c26391e", "group1"));
        targetedGroups.add(new UserGroup("12345678-e757-4474-b9c4-3f00a9ac17a0", "group2"));
        targetedGroups.add(new UserGroup("12345678-86a4-4237-aeb0-60bad29c1de0", "group3"));

        Assert.assertThat(groups, IsIterableContainingInAnyOrder.containsInAnyOrder(groups.toArray()));
    }

}
