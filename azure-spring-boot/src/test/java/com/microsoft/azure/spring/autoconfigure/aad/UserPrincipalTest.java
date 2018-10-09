/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.aad;

import com.microsoft.aad.adal4j.ClientCredential;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jwt.JWTClaimsSet;
import org.apache.commons.io.IOUtils;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.powermock.api.mockito.PowerMockito.doReturn;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AzureADGraphClient.class)
public class UserPrincipalTest {
    private AzureADGraphClient graphClientMock;

    @Mock
    private ClientCredential credential;

    @Mock
    private AADAuthenticationProperties aadAuthProps;

    @Mock
    private ServiceEndpointsProperties endpointsProps;

    @Before
    public void setup() {
        this.graphClientMock = PowerMockito.spy(new AzureADGraphClient(credential, aadAuthProps, endpointsProps));
    }

    @Test
    public void getAuthoritiesByUserGroups() throws Exception {
        final List<UserGroup> userGroups = new ArrayList<UserGroup>();
        userGroups.add(new UserGroup("this is group1", "group1"));

        doReturn(Constants.USERGROUPS_JSON)
                .when(graphClientMock, "getUserMembershipsV1", Constants.BEARER_TOKEN);
        PowerMockito.when(graphClientMock.getGroups(Constants.BEARER_TOKEN)).thenReturn(userGroups);
        Whitebox.setInternalState(graphClientMock, "aadTargetGroups", Constants.TARGETED_GROUPS);

        final Collection<? extends GrantedAuthority> authorities =
                graphClientMock.getGrantedAuthorities(Constants.BEARER_TOKEN);

        Assert.assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_group1")));
        Assert.assertFalse(authorities.contains(new SimpleGrantedAuthority("ROLE_group2")));
        Assert.assertFalse(authorities.contains(new SimpleGrantedAuthority("ROLE_group3")));
    }

    @Test
    public void getGroups() throws Exception {
        doReturn(Constants.USERGROUPS_JSON)
                .when(graphClientMock, "getUserMembershipsV1", Constants.BEARER_TOKEN);

        final List<UserGroup> groups = graphClientMock.getGroups(Constants.BEARER_TOKEN);
        final List<UserGroup> targetedGroups = new ArrayList<UserGroup>();
        targetedGroups.add(new UserGroup("12345678-7baf-48ce-96f4-a2d60c26391e", "group1"));
        targetedGroups.add(new UserGroup("12345678-e757-4474-b9c4-3f00a9ac17a0", "group2"));
        targetedGroups.add(new UserGroup("12345678-86a4-4237-aeb0-60bad29c1de0", "group3"));

        Assert.assertThat(groups, IsIterableContainingInAnyOrder.containsInAnyOrder(groups.toArray()));
    }

    @Test
    public void userPrinciplaIsSerializable() throws ParseException, IOException, ClassNotFoundException {
        final File tmpOutputFile = File.createTempFile("test-user-principal", "txt");

        try (final FileOutputStream fileOutputStream = new FileOutputStream(tmpOutputFile);
             final ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
             final FileInputStream fileInputStream = new FileInputStream(tmpOutputFile);
             final ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);){

            final JWSObject jwsObject = JWSObject.parse(Constants.JWT_TOKEN);
            final JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder().subject("fake-subject").build();
            final UserPrincipal principal = new UserPrincipal(jwsObject, jwtClaimsSet);

            objectOutputStream.writeObject(principal);

            final UserPrincipal serializedPrincipal = (UserPrincipal) objectInputStream.readObject();

            Assert.assertNotNull("Serialized UserPrincipal not null", serializedPrincipal);
            Assert.assertTrue("Serialized UserPrincipal kid not empty",
                    !StringUtils.isEmpty(serializedPrincipal.getKid()));
            Assert.assertNotNull("Serialized UserPrincipal claims not null.", serializedPrincipal.getClaims());
            Assert.assertTrue("Serialized UserPrincipal claims not empty.",
                    serializedPrincipal.getClaims().size() > 0);
        } finally {
            if (tmpOutputFile != null) {
                Files.deleteIfExists(tmpOutputFile.toPath());
            }
        }
    }
}
