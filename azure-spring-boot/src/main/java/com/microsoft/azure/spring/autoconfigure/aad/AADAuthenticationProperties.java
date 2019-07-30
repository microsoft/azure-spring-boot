/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.aad;

import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Validated
@ConfigurationProperties("azure.activedirectory")
@Data
@Slf4j
public class AADAuthenticationProperties {

    private static final String DEFAULT_SERVICE_ENVIRONMENT = "global";

    /**
     * Default UserGroup configuration.
     */
    private AbstractUserGroupProperties userGroup = new AADGraphUserGroupProperties();

    /**
     * Azure service environment/region name, e.g., cn, global
     */
    private String environment = DEFAULT_SERVICE_ENVIRONMENT;
    /**
     * Registered application ID in Azure AD.
     * Must be configured when OAuth2 authentication is done in front end
     */
    private String clientId;
    /**
     * API Access Key of the registered application.
     * Must be configured when OAuth2 authentication is done in front end
     */
    private String clientSecret;

    /**
     * Azure AD groups.
     */
    private List<String> activeDirectoryGroups = new ArrayList<>();

    /**
     * App ID URI which might be used in the <code>"aud"</code> claim of an <code>id_token</code>.
     */
    private String appIdUri;

    /**
     * Connection Timeout for the JWKSet Remote URL call.
     */
    private int jwtConnectTimeout = RemoteJWKSet.DEFAULT_HTTP_CONNECT_TIMEOUT; /* milliseconds */

    /**
     * Read Timeout for the JWKSet Remote URL call.
     */
    private int jwtReadTimeout = RemoteJWKSet.DEFAULT_HTTP_READ_TIMEOUT; /* milliseconds */

    /**
     * Size limit in Bytes of the JWKSet Remote URL call.
     */
    private int jwtSizeLimit = RemoteJWKSet.DEFAULT_HTTP_SIZE_LIMIT; /* bytes */

    /**
     * Azure Tenant ID.
     */
    private String tenantId;

    /**
     * If Telemetry events should be published to Azure AD.
     */
    private boolean allowTelemetry = true;

    public void setUserGroupWithBool(boolean microsoftGraphApi) {
        if (microsoftGraphApi) {
            if (!MicrosoftGraphUserGroupProperties.class.isInstance(this.userGroup)) {
                final AADGraphUserGroupProperties tempProperties = new AADGraphUserGroupProperties();
                final String overrideKey = this.userGroup.getKey().equals(tempProperties.getKey())
                                           ? null : this.userGroup.getKey();
                final String overrideValue = this.userGroup.getValue().equals(tempProperties.getValue())
                                           ? null : this.userGroup.getValue();
                final String overrideObjectIDKey = this.userGroup.getObjectIDKey()
                                           .equals(tempProperties.getObjectIDKey())
                                           ? null : this.userGroup.getObjectIDKey();
                final List<String> overrideAllowedGroups = this.userGroup.getAllowedGroups()
                                           .isEmpty() ? null : this.userGroup.getAllowedGroups();

                this.userGroup = new MicrosoftGraphUserGroupProperties();
                
                if (overrideKey != null) {
                    this.userGroup.key = overrideKey;
                }
                if (overrideValue != null) {
                    this.userGroup.value = overrideValue;
                }
                if (overrideObjectIDKey != null) {
                    this.userGroup.objectIDKey = overrideObjectIDKey;
                }
                if (overrideAllowedGroups != null) {
                    this.userGroup.allowedGroups = overrideAllowedGroups;
                }
            }
        } else {
            if (!AADGraphUserGroupProperties.class.isInstance(this.userGroup)) {
                this.userGroup = new AADGraphUserGroupProperties();
            }
        }
    }

    /**
     * If <code>true</code> activates the stateless auth filter {@link AADAppRoleStatelessAuthenticationFilter}.
     * The default is <code>false</code> which activates {@link AADAuthenticationFilter}.
     */
    private Boolean sessionStateless = false;

    @DeprecatedConfigurationProperty(reason = "Configuration moved to UserGroup class to keep UserGroup properties "
            + "together", replacement = "azure.activedirectory.user-group.allowed-groups")
    public List<String> getActiveDirectoryGroups() {
        return activeDirectoryGroups;
    }
    /**
     * Properties dedicated to changing the behavior of how the groups are mapped from the Azure AD response. Depending
     * on the graph API used the object will not be the same.
     */
    @Data
    public abstract static class AbstractUserGroupProperties {

        /**
         * Expected UserGroups that an authority will be granted to if found in the response from the MemeberOf Graph
         * API Call.
         */
        private List<String> allowedGroups = new ArrayList<>();

        /**
         * Key of the JSON Node to get from the Azure AD response object that will be checked to contain the {@code
         * azure.activedirectory.user-group.value}  to signify that this node is a valid {@code UserGroup}.
         */
        @NotEmpty
        private String key;


        /**
         * Value of the JSON Node identified by the {@code azure.activedirectory.user-group.key} to validate the JSON
         * Node is a UserGroup.
         */
        @NotEmpty
        private String value;

        /**
         * Key of the JSON Node containing the Azure Object ID for the {@code UserGroup}.
         */
        @NotEmpty
        private String objectIDKey;

        public AbstractUserGroupProperties(String keys, String value, String objectIDKey) {
            this.key = keys;
            this.value = value;
            this.objectIDKey = objectIDKey;
        }
    }

    /**
     * Properties for AAD Graph API used.
     */
    public static class AADGraphUserGroupProperties extends AbstractUserGroupProperties {

        /**
         * Key of the JSON Node to get from the Azure AD response object that will be checked to contain the {@code
         * azure.activedirectory.user-group.value}  to signify that this node is a valid {@code UserGroup}.
         */
        @NotEmpty
        private static String key = "objectType";

        /**
         * Value of the JSON Node identified by the {@code azure.activedirectory.user-group.key} to validate the JSON
         * Node is a UserGroup.
         */
        @NotEmpty
        private static String value = "Group";

        /**
         * Key of the JSON Node containing the Azure Object ID for the {@code UserGroup}.
         */
        @NotEmpty
        private static String objectIDKey = "objectId";

        public AADGraphUserGroupProperties() {
            super(key, value, objectIDKey);     
        }
    }

    /**
     * Properties for Microsoft Graph API used.
     */
    public static class MicrosoftGraphUserGroupProperties extends AbstractUserGroupProperties {

        /**
         * Key of the JSON Node to get from the Azure AD response object that will be checked to contain the {@code
         * azure.activedirectory.user-group.value}  to signify that this node is a valid {@code UserGroup}.
         */
        @NotEmpty
        private static String key = "@odata.type";

        /**
         * Value of the JSON Node identified by the {@code azure.activedirectory.user-group.key} to validate the JSON
         * Node is a UserGroup.
         */
        @NotEmpty
        private static String value = "#microsoft.graph.group";

        /**
         * Key of the JSON Node containing the Azure Object ID for the {@code UserGroup}.
         */
        @NotEmpty
        private static String objectIDKey = "id";

        public MicrosoftGraphUserGroupProperties() {
            super(key, value, objectIDKey); 
        }
    }

    /**
     * Validates at least one of the user group properties are populated.
     */    
    @PostConstruct
    public void validateUserGroupProperties() {
        if (this.sessionStateless) {
            if (!this.activeDirectoryGroups.isEmpty()) {
              log.warn("Group names are not supported if you set 'sessionSateless' to 'true'.");
            }
        } else if (this.activeDirectoryGroups.isEmpty() && this.getUserGroup().getAllowedGroups().isEmpty()) {
            throw new IllegalStateException("One of the User Group Properties must be populated. "
                + "Please populate azure.activedirectory.user-group.allowed-groups");
        }
    }
}
