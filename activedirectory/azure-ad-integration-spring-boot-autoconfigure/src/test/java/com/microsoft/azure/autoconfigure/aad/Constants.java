/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.autoconfigure.aad;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yaweiw on 8/1/2017.
 */
public class Constants {
    public static final String CLIENT_ID_PROPERTY = "azure.activedirectory.clientId";
    public static final String CLIENT_SECRET_PROPERTY = "azure.activedirectory.clientSecret";
    public static final String ALLOWED_ROLES_GROUPS_PROPERTY = "azure.activedirectory.allowedRolesGroups";
    public static final String CLIENT_ID = "real_client_id";
    public static final String CLIENT_SECRET = "real_client_secret";
    public static final List<String> ALLOWED_ROLES_GROUPS = new ArrayList<String>() {{
        add("group1");
        add("group2");
        add("group3");
    }};

    public static final String TOKEN_HEADER = "Authorization";
    public static final String BEARER_TOKEN = "Bearer real_jtw_bearer_token";
}
