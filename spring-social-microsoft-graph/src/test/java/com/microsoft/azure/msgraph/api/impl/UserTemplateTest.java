/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.msgraph.api.impl;

import com.microsoft.azure.msgraph.api.User;
import org.junit.Test;
import org.springframework.http.MediaType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class UserTemplateTest extends AbstractMicrosoftApiTest {
    @Test
    public void getUserProfileValid() throws Exception {
        mockServer
                .expect(requestTo(microsoft.getGraphAPI("me")))
                .andExpect(method(GET))
                .andExpect(header("Authorization", "Bearer access token"))
                .andRespond(withSuccess(jsonResource("user"), MediaType.APPLICATION_JSON));

        User user = microsoft.userOperations().getUserProfile();
        assertThat(user.getDisplayName()).isEqualTo("Anne Weiler");
        List<String> bussinessPhones = user.getBusinessPhones();
        assertThat(bussinessPhones).hasSize(1);
        assertThat(bussinessPhones.get(0)).isEqualTo("123");
        assertThat(user.getGivenName()).isEqualTo("Anne");
        assertThat(user.getId()).isEqualTo("16f5a7b6-5a15-4568-aa5a-31bb117e9967");
        assertThat(user.getJobTitle()).isEqualTo("Manufacturing Lead");
        assertThat(user.getMail()).isEqualTo("annew@CIE493742.onmicrosoft.com");
        assertThat(user.getMobilePhone()).isEqualTo("+1 3528700812");
        assertThat(user.getOfficeLocation()).isEqualTo("some location");
        assertThat(user.getPreferredLanguage()).isEqualTo("en-US");
        assertThat(user.getSurname()).isEqualTo("Weiler");
        assertThat(user.getUserPrincipalName()).isEqualTo("annew@CIE493742.onmicrosoft.com");
    }
}
