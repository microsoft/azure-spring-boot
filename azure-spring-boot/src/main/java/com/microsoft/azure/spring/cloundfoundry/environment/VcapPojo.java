/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.cloundfoundry.environment;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class VcapPojo {
    private String serviceBrokerName;
    private Map<String, String> credentials = new HashMap<>();
    private String label;
    private String serviceInstanceName;
    private String servicePlan;
    private String provider;
    private String syslogDrainUrl;
    private String[] tags;
    private String[] volumeMounts;

    public String getServiceBrokerName() {
        return serviceBrokerName;
    }

    public void setServiceBrokerName(String serviceBrokerName) {
        this.serviceBrokerName = serviceBrokerName;
    }

    public Map<String, String> getCredentials() {
        return credentials;
    }

    public void setCredentials(Map<String, String> credentials) {
        this.credentials = credentials;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getServiceInstanceName() {
        return serviceInstanceName;
    }

    public void setServiceInstanceName(String serviceName) {
        this.serviceInstanceName = serviceName;
    }

    public String getServicePlan() {
        return servicePlan;
    }

    public void setServicePlan(String servicePlan) {
        this.servicePlan = servicePlan;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getSyslogDrainUrl() {
        return syslogDrainUrl;
    }

    public void setSyslogDrainUrl(String syslogDrainUrl) {
        this.syslogDrainUrl = syslogDrainUrl;
    }

    @SuppressFBWarnings("EI_EXPOSE_REP")
    public String[] getTags() {
        return tags;
    }

    @SuppressFBWarnings("EI_EXPOSE_REP")
    public void setTags(String[] tags) {
        this.tags = tags;
    }

    @SuppressFBWarnings("EI_EXPOSE_REP")
    public String[] getVolumeMounts() {
        return volumeMounts;
    }

    @SuppressFBWarnings("EI_EXPOSE_REP")
    public void setVolumeMounts(String[] volumeMounts) {
        this.volumeMounts = volumeMounts;
    }

    @Override
    public String toString() {
        return "VcapPojo [serviceBrokerName=" + serviceBrokerName
                + ", credentials=" + credentials + ", label=" + label
                + ", serviceName=" + serviceInstanceName + ", servicePlan="
                + servicePlan + ", provider=" + provider + ", syslogDrainUrl="
                + syslogDrainUrl + ", tags=" + Arrays.toString(tags)
                + ", volumeMounts=" + Arrays.toString(volumeMounts) + "]";
    }

}
