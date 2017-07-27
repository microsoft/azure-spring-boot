/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.msgraph.api.impl;

import com.microsoft.azure.msgraph.api.UserProfile;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class MicrosoftTemplateTest {
    private MicrosoftTemplate microsoftTemplate;
    private MockRestServiceServer mockServer;
    private HttpHeaders responseHeaders;

    @Before
    public void setUp() throws Exception {
        microsoftTemplate = new MicrosoftTemplate("access token");
        mockServer = MockRestServiceServer.createServer(microsoftTemplate.getRestTemplate());
        responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
    }

    @Test
    public void getUserProfileValid() throws Exception {
        mockServer
                .expect(requestTo("https://graph.microsoft.com/v1.0/me"))
                .andExpect(method(GET))
                .andExpect(header("Authorization", "Bearer access token"))
                .andRespond(withSuccess(jsonResource("user"), MediaType.APPLICATION_JSON));

        UserProfile userProfile = microsoftTemplate.userOperations().getUserProfile();
        assertThat(userProfile.getDisplayName()).isEqualTo("Anne Weiler");
        List<String> bussinessPhones = userProfile.getBusinessPhones();
        assertThat(bussinessPhones).hasSize(1);
        assertThat(bussinessPhones.get(0)).isEqualTo("123");
        assertThat(userProfile.getGivenName()).isEqualTo("Anne");
        assertThat(userProfile.getId()).isEqualTo("16f5a7b6-5a15-4568-aa5a-31bb117e9967");
        assertThat(userProfile.getJobTitle()).isEqualTo("Manufacturing Lead");
        assertThat(userProfile.getMail()).isEqualTo("annew@CIE493742.onmicrosoft.com");
        assertThat(userProfile.getMobilePhone()).isEqualTo("+1 3528700812");
        assertThat(userProfile.getOfficeLocation()).isEqualTo("some location");
        assertThat(userProfile.getPreferredLanguage()).isEqualTo("en-US");
        assertThat(userProfile.getSurname()).isEqualTo("Weiler");
        assertThat(userProfile.getUserPrincipalName()).isEqualTo("annew@CIE493742.onmicrosoft.com");
    }

    private Resource jsonResource(String filename) {
        return new ClassPathResource(filename + ".json");
    }
}
