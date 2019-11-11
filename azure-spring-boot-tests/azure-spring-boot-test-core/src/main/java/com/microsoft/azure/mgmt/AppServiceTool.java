/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.mgmt;

import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.appservice.*;
import com.microsoft.azure.management.graphrbac.BuiltInRole;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;
import com.microsoft.azure.management.resources.fluentcore.utils.SdkContext;
import com.microsoft.azure.utils.ManagementUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

@Slf4j
public class AppServiceTool {

    private WebApps webApps;

    public AppServiceTool(Access access) {
        webApps = Azure
                .authenticate(access.credentials())
                .withSubscription(access.subscription())
                .webApps();
    }

    public WebApp createAppService(String resourceGroup, String prefix, Map<String, String> settings) {
        final String appName = SdkContext.randomResourceName(prefix, 20);

        log.info("Creating web app " + appName);

        final WebApp app = webApps
                .define(appName)
                .withRegion(Region.US_WEST)
                .withNewResourceGroup(resourceGroup)
                .withNewLinuxPlan(PricingTier.STANDARD_S1)
                .withBuiltInImage(RuntimeStack.JAVA_8_JRE8)
                .withSystemAssignedManagedServiceIdentity()
                .withSystemAssignedIdentityBasedAccessToCurrentResourceGroup(BuiltInRole.OWNER)
                .withJavaVersion(JavaVersion.JAVA_8_NEWEST)
                .withWebContainer(WebContainer.JAVA_8)
                .withAppSettings(settings)
                .create();

        log.info("Created web app " + app.name());
        return app;
    }

    public void deployJARToAppService(WebApp app, String jarFilePath) throws Exception {
        log.info("Deploying a spring boot app " + jarFilePath + " to " + app.name() + " through FTP...");

        try (FileInputStream fis = new FileInputStream(new File(jarFilePath))) {
            ManagementUtils.uploadFileToWebAppWwwRoot(app.getPublishingProfile(), "app.jar", fis);
        }

        log.info("Deployment to web app " + app.name() + " completed");
    }

}
