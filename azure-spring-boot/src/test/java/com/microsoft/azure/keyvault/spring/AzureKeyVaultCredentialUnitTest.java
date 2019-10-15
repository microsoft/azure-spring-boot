/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.keyvault.spring;

import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.azure.utils.AADAuthUtil;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AzureKeyVaultCredentialUnitTest {

    @Test(expected = RuntimeException.class)
    public void testDoAuthenticationRejctIfInvalidCredential() {
        final AzureKeyVaultCredential keyVaultCredential = new AzureKeyVaultCredential("fakeClientId",
                "fakeClientKey",
                Constants.TOKEN_ACQUIRE_TIMEOUT_SECS);
        keyVaultCredential.doAuthenticate("https://fakeauthorizationurl.com", "keyvault", "scope");
    }

    @Test
    public void testDoAuthenticationPass() throws Exception {
        final MockAADAuthUtil mockAADAuthUtil = new MockAADAuthUtil("token1", 11L);
        final AzureKeyVaultCredential keyVaultCredential = new AzureKeyVaultCredential("fakeClientId",
                "fakeClientKey",
                Constants.TOKEN_ACQUIRE_TIMEOUT_SECS,
                mockAADAuthUtil);
        final String token = keyVaultCredential.doAuthenticate("https://fakeauthorizationurl.com", "keyvault", "scope");
        //assert token from cache
        mockAADAuthUtil.updateToken("token2");
        assertThat(keyVaultCredential.doAuthenticate("https://fakeauthorizationurl.com",
                "keyvault",
                "scope")).isEqualTo(token);
        //assert token refresh
        Thread.sleep(1000L);
        assertThat(keyVaultCredential.doAuthenticate("https://fakeauthorizationurl.com",
                "keyvault",
                "scope")).isNotEqualTo(token);
    }

    class MockAADAuthUtil extends AADAuthUtil {
        private AuthenticationResult result;

        public void updateToken(String token) {
            result = new AuthenticationResult("mockType",
                    token,
                    "mockRefreshToken",
                    result.getExpiresAfter(),
                    "mockIdToken",
                    null,
                    true);
        }

        public MockAADAuthUtil(String token, long expiresIn) {
            this.result = new AuthenticationResult("mockType",
                    token,
                    "mockRefreshToken",
                    expiresIn,
                    "mockIdToken",
                    null,
                    true);
        }

        @Override
        public AuthenticationResult getToken(String authorization,
                                             String resource,
                                             String clientId,
                                             String clientKey,
                                             long tokenAcquireTimeout) {
            return result;
        }
    }
}
