/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.aad;

import java.util.Arrays;
import java.util.List;

public class Constants {
    public static final String SERVICE_ENVIRONMENT_PROPERTY = "azure.activedirectory.environment";
    public static final String CLIENT_ID_PROPERTY = "azure.activedirectory.client-id";
    public static final String CLIENT_SECRET_PROPERTY = "azure.activedirectory.client-secret";
    public static final String TARGETED_GROUPS_PROPERTY = "azure.activedirectory.active-directory-groups";
    public static final String TENANT_ID_PROPERTY = "azure.activedirectory.tenant-id";

    public static final String DEFAULT_ENVIRONMENT = "global";
    public static final String CLIENT_ID = "real_client_id";
    public static final String CLIENT_SECRET = "real_client_secret";
    public static final List<String> TARGETED_GROUPS = Arrays.asList("group1", "group2", "group3");

    public static final String TOKEN_HEADER = "Authorization";
    public static final String BEARER_TOKEN = "Bearer real_jtw_bearer_token";

    public static final String USERGROUPS_JSON = "{\n" +
            "    \"odata.metadata\": \"https://graph.windows.net/myorganization/$metadata#directoryObjects\",\n" +
            "    \"value\": [\n" +
            "        {\n" +
            "            \"odata.type\": \"Microsoft.DirectoryServices.Group\",\n" +
            "            \"objectType\": \"Group\",\n" +
            "            \"objectId\": \"12345678-7baf-48ce-96f4-a2d60c26391e\",\n" +
            "            \"deletionTimestamp\": null,\n" +
            "            \"description\": \"this is group1\",\n" +
            "            \"dirSyncEnabled\": true,\n" +
            "            \"displayName\": \"group1\",\n" +
            "            \"lastDirSyncTime\": \"2017-08-02T12:54:37Z\",\n" +
            "            \"mail\": null,\n" +
            "            \"mailNickname\": \"something\",\n" +
            "            \"mailEnabled\": false,\n" +
            "            \"onPremisesDomainName\": null,\n" +
            "            \"onPremisesNetBiosName\": null,\n" +
            "            \"onPremisesSamAccountName\": null,\n" +
            "            \"onPremisesSecurityIdentifier\": \"S-1-5-21-1234567885-903363285-719344707-285039\",\n" +
            "            \"provisioningErrors\": [],\n" +
            "            \"proxyAddresses\": [],\n" +
            "            \"securityEnabled\": true\n" +
            "        },\n" +
            "        {\n" +
            "            \"odata.type\": \"Microsoft.DirectoryServices.Group\",\n" +
            "            \"objectType\": \"Group\",\n" +
            "            \"objectId\": \"12345678-e757-4474-b9c4-3f00a9ac17a0\",\n" +
            "            \"deletionTimestamp\": null,\n" +
            "            \"description\": null,\n" +
            "            \"dirSyncEnabled\": true,\n" +
            "            \"displayName\": \"group2\",\n" +
            "            \"lastDirSyncTime\": \"2017-08-09T13:45:03Z\",\n" +
            "            \"mail\": null,\n" +
            "            \"mailNickname\": \"somethingelse\",\n" +
            "            \"mailEnabled\": false,\n" +
            "            \"onPremisesDomainName\": null,\n" +
            "            \"onPremisesNetBiosName\": null,\n" +
            "            \"onPremisesSamAccountName\": null,\n" +
            "            \"onPremisesSecurityIdentifier\": \"S-1-5-21-1234567885-903363285-719344707-28565\",\n" +
            "            \"provisioningErrors\": [],\n" +
            "            \"proxyAddresses\": [],\n" +
            "            \"securityEnabled\": true\n" +
            "        },\n" +
            "        {\n" +
            "            \"odata.type\": \"Microsoft.DirectoryServices.Group\",\n" +
            "            \"objectType\": \"Group\",\n" +
            "            \"objectId\": \"12345678-86a4-4237-aeb0-60bad29c1de0\",\n" +
            "            \"deletionTimestamp\": null,\n" +
            "            \"description\": \"this is group3\",\n" +
            "            \"dirSyncEnabled\": true,\n" +
            "            \"displayName\": \"group3\",\n" +
            "            \"lastDirSyncTime\": \"2017-08-09T05:41:43Z\",\n" +
            "            \"mail\": null,\n" +
            "            \"mailNickname\": \"somethingelse\",\n" +
            "            \"mailEnabled\": false,\n" +
            "            \"onPremisesDomainName\": null,\n" +
            "            \"onPremisesNetBiosName\": null,\n" +
            "            \"onPremisesSamAccountName\": null,\n" +
            "            \"onPremisesSecurityIdentifier\": \"S-1-5-21-1234567884-1604012920-1887927527-14401381\",\n" +
            "            \"provisioningErrors\": [],\n" +
            "            \"proxyAddresses\": [],\n" +
            "            \"securityEnabled\": true\n" +
            "        }" +
            "],\n" +
            "    \"odata.nextLink\": \"directoryObjects/$/Microsoft.DirectoryServices.User/" +
            "12345678-2898-434a-a370-8ec974c2fb57/memberOf?$skiptoken=X'4453707407000100000000" +
            "00000000100000009D29CBA7B45D854A84FF7F9B636BD9DC000000000000000000000017312E322E3" +
            "834302E3131333535362E312E342E3233333100000000'\"\n" +
            "}";
}
