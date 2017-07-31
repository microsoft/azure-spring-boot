/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.msgraph.connect;

import com.microsoft.azure.msgraph.api.Microsoft;
import com.microsoft.azure.msgraph.api.impl.MicrosoftTemplate;
import org.springframework.social.oauth2.AbstractOAuth2ServiceProvider;
import org.springframework.social.oauth2.OAuth2Template;

public class MicrosoftServiceProvider extends AbstractOAuth2ServiceProvider<Microsoft> {
    public MicrosoftServiceProvider(String clientId, String clientSecret) {
        super(getOAuth2Template(clientId, clientSecret));
    }

    private static OAuth2Template getOAuth2Template(String appId, String appSecret) {
        final OAuth2Template oAuth2Template = new OAuth2Template(appId, appSecret,
                "https://login.microsoftonline.com/common/oauth2/v2.0/authorize",
                "https://login.microsoftonline.com/common/oauth2/v2.0/token");
        oAuth2Template.setUseParametersForClientAuthentication(true);
        return oAuth2Template;
    }

    @Override
    public Microsoft getApi(String accessToken) {
        return new MicrosoftTemplate(accessToken);
    }
}
