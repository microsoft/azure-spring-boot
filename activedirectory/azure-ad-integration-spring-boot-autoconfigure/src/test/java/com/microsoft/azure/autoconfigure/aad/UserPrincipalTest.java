package com.microsoft.azure.autoconfigure.aad;

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
        List<UserGroup> userGroups = new ArrayList<UserGroup>();
        userGroups.add(new UserGroup("Microsoft.DirectoryServices.Group", "Group", "this is group1", "group1"));

        Collection<? extends GrantedAuthority> authorities = principal.getAuthoritiesByUserGroups(userGroups, Constants.TARGETED_GROUPS);
        Assert.assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_group1")));
        Assert.assertFalse(authorities.contains(new SimpleGrantedAuthority("ROLE_group2")));
        Assert.assertFalse(authorities.contains(new SimpleGrantedAuthority("ROLE_group3")));
    }

    @Test
    public void getGroups() throws Exception {
        PowerMockito.mockStatic(AzureADGraphClient.class);
        Mockito.when(AzureADGraphClient.getUserMembershipsV1(Constants.BEARER_TOKEN)).thenReturn(Constants.UserGroups_JSON);

        final UserPrincipal principal = new UserPrincipal();

        List<UserGroup> groups = principal.getGroups(Constants.BEARER_TOKEN);
        List<UserGroup> targetedGroups = new ArrayList<UserGroup>();
        targetedGroups.add(new UserGroup("Microsoft.DirectoryServices.Group", "Group", "this is group1", "group1"));
        targetedGroups.add(new UserGroup("Microsoft.DirectoryServices.Group", "Group", null, "group2"));
        targetedGroups.add(new UserGroup("Microsoft.DirectoryServices.Group", "Group", "this is group3", "group3"));

        Assert.assertThat(groups, IsIterableContainingInAnyOrder.containsInAnyOrder(groups.toArray()));
    }

}