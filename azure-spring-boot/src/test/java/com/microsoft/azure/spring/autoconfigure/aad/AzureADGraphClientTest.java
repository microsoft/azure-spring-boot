/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.aad;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.security.core.GrantedAuthority;

import com.microsoft.aad.adal4j.ClientCredential;

import io.jsonwebtoken.lang.Assert;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AzureADGraphClient.class)
public class AzureADGraphClientTest {

    private AzureADGraphClient adGraphClient;

    @Mock
    private ClientCredential credential;

    @Mock
    private AADAuthenticationProperties aadAuthProps;

    @Mock
    private ServiceEndpointsProperties endpointsProps;
    
    @Before
    public void setup() throws Exception {
        adGraphClient = PowerMockito.spy(new AzureADGraphClient(credential, aadAuthProps, endpointsProps));
    }

     @Test
     public void testConvetGroupToGrantedAuthorities() {
         
         final UserGroup userGroup = new UserGroup("testId", "Test_Group");
         final List<UserGroup> userGroups =  new ArrayList<>();
         userGroups.add(userGroup);
         final Set<GrantedAuthority> authorities = adGraphClient.convetGroupToGrantedAuthorities(userGroups);
         Assert.notEmpty(authorities);
     }
}
