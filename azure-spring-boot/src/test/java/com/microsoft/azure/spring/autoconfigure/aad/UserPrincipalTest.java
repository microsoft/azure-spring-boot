/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.aad;

import com.microsoft.aad.adal4j.ClientCredential;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jwt.JWTClaimsSet;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.doReturn;


@RunWith(MockitoJUnitRunner.class)
public class UserPrincipalTest {

    private AzureADGraphClient graphClient;
    private ClientCredential credential;
    private AADAuthenticationProperties aadAuthProps;

    @Mock
    private ServiceEndpointsProperties endpointsProps;

    @Mock
    private AADGraphHttpClient aadGraphHttpClient;


    @Before
    public void setUp() throws Exception {
        credential = new ClientCredential("test", "password");
        aadAuthProps = new AADAuthenticationProperties();
    }

    @Test
    public void getAuthoritiesByUserGroups() throws Exception {


        aadAuthProps.setActiveDirectoryGroups(Collections.singletonList("group1"));
        this.graphClient = new AzureADGraphClient(credential,
                aadAuthProps, endpointsProps, aadGraphHttpClient);

        doReturn(Constants.USERGROUPS_JSON)
                .when(aadGraphHttpClient).getMemberships(Constants.BEARER_TOKEN);
        final Collection<? extends GrantedAuthority> authorities =
                graphClient.getGrantedAuthorities(Constants.BEARER_TOKEN);

      assertThat(authorities).extracting(GrantedAuthority::getAuthority).containsExactly("ROLE_group1");
    }

    @Test
    public void getGroups() throws Exception {
        doReturn(Constants.USERGROUPS_JSON)
                .when(aadGraphHttpClient.getMemberships(Constants.BEARER_TOKEN));

        final List<UserGroup> groups = graphClient.getGroups(Constants.BEARER_TOKEN);
        final List<UserGroup> targetedGroups = new ArrayList<>();
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
                Files.deleteIfExists(tmpOutputFile.toPath());

        }
    }
}
