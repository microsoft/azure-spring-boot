/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.cloundfoundry.environment;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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
                final ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, false);

                final Map<String, List<VcapServiceConfig>> servicesMap = objectMapper.readValue(vcapServices,
                        new TypeReference<Map<String, List<VcapServiceConfig>>>() { });
                final Set<Map.Entry<String, List<VcapServiceConfig>>> services = servicesMap.entrySet();

                if (services != null) {
                    final Iterator<Map.Entry<String, List<VcapServiceConfig>>> serviceIterator = services.iterator();

                    while (serviceIterator.hasNext()) {
                        final Map.Entry<String, List<VcapServiceConfig>> serviceEntry = serviceIterator.next();
                        final String name = serviceEntry.getKey();

                        if (name.startsWith(AZURE) || USER_PROVIDED.equals(name)) {
                            final List<VcapServiceConfig> azureServices = serviceEntry.getValue();

                            results.addAll(azureServices.stream()
                                    .map(service -> parseService(name, service, vcapServices))
                                    .filter(vcapPojo -> vcapPojo != null).collect(Collectors.toList()));
                        }
                    }
                }
            } catch (IOException e) {
                LOGGER.error("Error parsing " + vcapServices, e);
            }
        }

        result.setPojos(results.toArray(new VcapPojo[results.size()]));
        return result;
    }

    private VcapPojo parseService(String serviceBrokerName, VcapServiceConfig serviceConfig, String vCapServices) {
        final VcapPojo result = new VcapPojo();

        final Map<String, String> credentials = serviceConfig.getCredentials();

        if (USER_PROVIDED.equals(serviceBrokerName)) {
            if (credentials == null) {
                return null;
            }

            final String userServiceBrokerName = credentials.remove(AZURE_SERVICE_BROKER_NAME);
            if (userServiceBrokerName == null) {
                return null;
            }

            result.setServiceBrokerName(userServiceBrokerName);
            final String userServicePlan = credentials.remove(AZURE_SERVICE_PLAN);
            serviceConfig.setPlan(userServicePlan);
            serviceConfig.setCredentials(credentials);
        } else {
            result.setServiceBrokerName(serviceBrokerName);
            serviceConfig.setPlan(serviceConfig.getPlan());
            if (credentials == null) {
                LOGGER.error("Found " + serviceBrokerName + ", but missing " + CREDENTIALS + " : " + vCapServices);
            }
        }

        result.setServiceConfig(serviceConfig);
        return result;
    }

    private void log(String msg) {
        if (logFlag) {
            LOGGER.info(msg);
        }
    }
}
