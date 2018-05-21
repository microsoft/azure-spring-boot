/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.cloundfoundry.environment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parses VCAP_SERVICES environment variable and sets corresponding property values.
 * <p>
 * Note that this class gets invoked before Spring creates the logging subsystem, so
 * we just use System.out.println instead.
 */
@Service
@Configuration
public class VcapProcessor implements EnvironmentPostProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(VcapProcessor.class);

    public static final String VCAP_SERVICES = "VCAP_SERVICES";
    public static final String LOG_VARIABLE = "COM_MICROSOFT_AZURE_CLOUDFOUNDRY_SERVICE_LOG";
    private static final String AZURE = "azure-";
    private static final String USER_PROVIDED = "user-provided";
    private static final String AZURE_SERVICE_BROKER_NAME = "azure-service-broker-name";
    private static final String AZURE_SERVICE_PLAN = "azure-service-plan";
    private static final String CREDENTIALS = "credentials";
    private static final String LABEL = "label";
    private static final String NAME = "name";
    private static final String PLAN = "plan";
    private static final String PROVIDER = "provider";
    private static final String SYSLOG_DRAIN_URL = "syslog_drain_url";
    private static final String TAGS = "tags";
    private static final String VOLUME_MOUNTS = "volume_mounts";
    private boolean logFlag = false;

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment confEnv,
                                       SpringApplication app) {
        final Map<String, Object> environment = confEnv.getSystemEnvironment();

        final String logValue = (String) environment.get(VcapProcessor.LOG_VARIABLE);
        if ("true".equals(logValue)) {
            logFlag = true;
        }

        log("VcapParser.postProcessEnvironment: Start");
        final String vcapServices = (String) environment
                .get(VcapProcessor.VCAP_SERVICES);
        final VcapResult result = parse(vcapServices);
        result.setLogFlag(logFlag);
        result.setConfEnv(confEnv);
        result.populateProperties();
        log("VcapParser.postProcessEnvironment: End");
    }

    public VcapResult parse(String vcapServices) {
        final VcapResult result = new VcapResult();

        final List<VcapPojo> results = new ArrayList<>();

        log("VcapParser.parse:  vcapServices = " + vcapServices);
        if (vcapServices != null) {
            try {
                final JSONObject json = new JSONObject(vcapServices);
                final JSONArray names = json.names();

                if (names != null) {
                    for (int i = 0; i < names.length(); i++) {
                        final String name = (String) names.get(i);
                        if (name.startsWith(AZURE) || USER_PROVIDED.equals(name)) {
                            final JSONArray azureService = json.getJSONArray(name);
                            final int numElements = azureService.length();
                            for (int index = 0; index < numElements; index++) {
                                final VcapPojo pojo = parseService(name, azureService,
                                        vcapServices, index);
                                
                                if (pojo != null) {
                                  results.add(pojo);
                                }
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                LOGGER.error("Error parsing " + vcapServices, e);
            }
        }

        result.setPojos(results.toArray(new VcapPojo[results.size()]));
        return result;
    }

    private VcapPojo parseService(String serviceBrokerName,
                                  JSONArray azureService, String vCapServices, int index) {
        final VcapPojo result = new VcapPojo();

        try {
            final JSONObject service = azureService.getJSONObject(index);
            result.setLabel(parseString(service, LABEL));
            result.setProvider(parseString(service, PROVIDER));
            result.setServiceInstanceName(parseString(service, NAME));
            result.setSyslogDrainUrl(parseString(service, SYSLOG_DRAIN_URL));
            result.setTags(parseStringArray(service.getJSONArray(TAGS)));
            result.setVolumeMounts(parseStringArray(service
                    .getJSONArray(VOLUME_MOUNTS)));

            final JSONObject credObject = service.getJSONObject(CREDENTIALS);

            if (USER_PROVIDED.equals(serviceBrokerName)) {
                if (credObject == null) {
                    return null;
                }
                
                final HashMap<String, String> credentials = new HashMap<>();
                parseMap(credObject, credentials);
                
                final String userServiceBrokerName = credentials.remove(AZURE_SERVICE_BROKER_NAME);
                if (userServiceBrokerName == null) {
                    return null;
                }
                
                result.setServiceBrokerName(userServiceBrokerName);
                final String userServicePlan = credentials.remove(AZURE_SERVICE_PLAN);
                result.setServicePlan(userServicePlan);
                result.setCredentials(credentials);
            } else {
                result.setServiceBrokerName(serviceBrokerName);
                result.setServicePlan(parseString(service, PLAN));
                if (credObject == null) {
                    LOGGER.error("Found " + serviceBrokerName + ", but missing "
                            + CREDENTIALS + " : " + vCapServices);
                } else {
                    parseMap(credObject, result.getCredentials());
                }
            }

        } catch (JSONException e) {
            LOGGER.error("Found " + serviceBrokerName + ", but missing "
                    + CREDENTIALS + " : " + vCapServices, e);
        }
        return result;
    }

    private String[] parseStringArray(JSONArray strings) {
        final List<String> results = new ArrayList<>();

        for (int i = 0; i < strings.length(); i++) {
            try {
                results.add((String) strings.get(i));
            } catch (JSONException e) {
                LOGGER.error("Error parsing " + strings, e);
            }
        }

        return results.toArray(new String[results.size()]);
    }

    private void parseMap(JSONObject mapObject, Map<String, String> target) {
        final JSONArray keys = mapObject.names();
        for (int i = 0; i < keys.length(); i++) {
            try {
                final String key = (String) keys.get(i);
                final String value = mapObject.getString(key);
                target.put(key, value);
            } catch (JSONException e) {
                LOGGER.error("Error parsing " + mapObject, e);
            }
        }
    }

    private String parseString(JSONObject service, String key) {
        String result = null;

        try {
            if (!service.isNull(key)) {
                result = service.getString(key);
            }
        } catch (JSONException e) {
            LOGGER.error("Error parsing " + service, e);
        }

        return result;
    }

    private void log(String msg) {
        if (logFlag) {
            LOGGER.info(msg);
        }
    }
}
