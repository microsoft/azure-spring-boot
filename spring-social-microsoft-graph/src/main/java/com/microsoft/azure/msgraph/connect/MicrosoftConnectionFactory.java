/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.msgraph.connect;

import com.microsoft.azure.msgraph.api.Microsoft;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;

public class MicrosoftConnectionFactory extends OAuth2ConnectionFactory<Microsoft> {
    public MicrosoftConnectionFactory(String clientId, String clientSecret) {
        super("microsoft", new MicrosoftServiceProvider(clientId, clientSecret), new MicrosoftAdapter());
    }
}
