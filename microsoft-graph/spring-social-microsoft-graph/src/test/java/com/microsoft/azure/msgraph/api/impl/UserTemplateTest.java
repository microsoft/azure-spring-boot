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

        final User user = microsoft.userOperations().getUserProfile();
        assertThat(user.displayName).isEqualTo("Anne Weiler");
        final List<String> bussinessPhones = user.businessPhones;
        assertThat(bussinessPhones).hasSize(1);
        assertThat(bussinessPhones.get(0)).isEqualTo("123");
        assertThat(user.givenName).isEqualTo("Anne");
        assertThat(user.id).isEqualTo("16f5a7b6-5a15-4568-aa5a-31bb117e9967");
        assertThat(user.jobTitle).isEqualTo("Manufacturing Lead");
        assertThat(user.mail).isEqualTo("annew@CIE493742.onmicrosoft.com");
        assertThat(user.mobilePhone).isEqualTo("+1 3528700812");
        assertThat(user.officeLocation).isEqualTo("some location");
        assertThat(user.preferredLanguage).isEqualTo("en-US");
        assertThat(user.surname).isEqualTo("Weiler");
        assertThat(user.userPrincipalName).isEqualTo("annew@CIE493742.onmicrosoft.com");
    }
}
