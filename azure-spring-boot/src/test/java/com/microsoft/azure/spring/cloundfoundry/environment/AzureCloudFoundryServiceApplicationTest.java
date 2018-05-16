/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.cloundfoundry.environment;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {VcapProcessor.class})
public class AzureCloudFoundryServiceApplicationTest {

    private static final Logger LOG = LoggerFactory
            .getLogger(AzureCloudFoundryServiceApplicationTest.class);

    @Autowired
    private VcapProcessor parser;

    @Test
    @SuppressFBWarnings("DM_DEFAULT_ENCODING")
    public void testVcapSingleService() throws IOException {
        final Resource resource = new ClassPathResource("/vcap1.json");
        final String content;
        try {
            content = new String(
                    Files.readAllBytes(Paths.get(resource.getURI())));
            final VcapResult result = parser.parse(content);
            final VcapPojo[] pojos = result.getPojos();
            assertNotNull(pojos);
            assertEquals(1, pojos.length);
            final VcapPojo pojo = pojos[0];

            LOG.debug("pojo = " + pojo);
            assertEquals(3, pojo.getCredentials().size());
            assertEquals(2, pojo.getTags().length);
            assertEquals(0, pojo.getVolumeMounts().length);
            assertEquals("azure-storage", pojo.getLabel());
            assertEquals("provider", pojo.getProvider());
            assertEquals("azure-storage", pojo.getServiceBrokerName());
            assertEquals("azure-storage-service", pojo.getServiceInstanceName());
            assertEquals("standard", pojo.getServicePlan());
            assertNull(pojo.getSyslogDrainUrl());
            assertEquals("Azure", pojo.getTags()[0]);
            assertEquals("Storage", pojo.getTags()[1]);

            assertEquals("pak",
                    pojo.getCredentials().get("primary_access_key"));
            assertEquals("sak",
                    pojo.getCredentials().get("secondary_access_key"));
            assertEquals("sam",
                    pojo.getCredentials().get("storage_account_name"));
        } catch (IOException e) {
            LOG.error("Error reading json file", e);
            throw e;
        }
    }

    @Test
    @SuppressFBWarnings("DM_DEFAULT_ENCODING")
    public void testVcapSingleServiceWithNulls() throws IOException {
        final Resource resource = new ClassPathResource("/vcap2.json");
        final String content;
        try {
            content = new String(
                    Files.readAllBytes(Paths.get(resource.getURI())));
            final VcapResult result = parser.parse(content);
            final VcapPojo[] pojos = result.getPojos();
            assertNotNull(pojos);
            assertEquals(1, pojos.length);
            final VcapPojo pojo = pojos[0];

            LOG.debug("pojo = " + pojo);
            assertEquals(4, pojo.getCredentials().size());
            assertEquals(0, pojo.getTags().length);
            assertEquals(0, pojo.getVolumeMounts().length);
            assertEquals("azure-documentdb", pojo.getLabel());
            assertNull(pojo.getProvider());
            assertEquals("azure-documentdb", pojo.getServiceBrokerName());
            assertEquals("mydocumentdb", pojo.getServiceInstanceName());
            assertEquals("standard", pojo.getServicePlan());
            assertNull(pojo.getSyslogDrainUrl());

            assertEquals("docdb123mj",
                    pojo.getCredentials().get("documentdb_database_id"));
            assertEquals("dbs/ZFxCAA==/",
                    pojo.getCredentials().get("documentdb_database_link"));
            assertEquals("https://hostname:443/",
                    pojo.getCredentials().get("documentdb_host_endpoint"));
            assertEquals(
                    "3becR7JFnWamMvGwWYWWTV4WpeNhN8tOzJ74yjAxPKDpx65q2lYz60jt8WXU6HrIKrAIwhs0Hglf0123456789==",
                    pojo.getCredentials().get("documentdb_master_key"));
        } catch (IOException e) {
            LOG.error("Error reading json file", e);
            throw e;
        }
    }

    @Test
    @SuppressFBWarnings("DM_DEFAULT_ENCODING")
    public void testVcapUserProvidedService() throws IOException {
        final Resource resource = new ClassPathResource("/vcap3.json");
        final String content;
        try {
            content = new String(
                    Files.readAllBytes(Paths.get(resource.getURI())));
            final VcapResult result = parser.parse(content);
            final VcapPojo[] pojos = result.getPojos();
            assertNotNull(pojos);
            assertEquals(1, pojos.length);
            final VcapPojo pojo = pojos[0];

            LOG.debug("pojo = " + pojo);
            assertEquals(4, pojo.getCredentials().size());
            assertEquals(0, pojo.getTags().length);
            assertEquals(0, pojo.getVolumeMounts().length);
            assertEquals("user-provided", pojo.getLabel());
            assertNull(pojo.getProvider());
            assertEquals("azure-documentdb", pojo.getServiceBrokerName());
            assertEquals("mydocumentdb", pojo.getServiceInstanceName());
            assertEquals("standard", pojo.getServicePlan());
            assertNull(pojo.getSyslogDrainUrl());

            assertEquals("docdb123mj",
                    pojo.getCredentials().get("documentdb_database_id"));
            assertEquals("dbs/ZFxCAA==/",
                    pojo.getCredentials().get("documentdb_database_link"));
            assertEquals("https://hostname:443/",
                    pojo.getCredentials().get("documentdb_host_endpoint"));
            assertEquals(
                    "3becR7JFnWamMvGwWYWWTV4WpeNhN8tOzJ74yjAxPKDpx65q2lYz60jt8WXU6HrIKrAIwhs0Hglf0123456789==",
                    pojo.getCredentials().get("documentdb_master_key"));
        } catch (IOException e) {
            LOG.error("Error reading json file", e);
            throw e;
        }
    }

}
