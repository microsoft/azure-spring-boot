/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.cloudfoundry.service.sample.documentdb;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DocumentDbRestController {

    private static final Logger LOG = LoggerFactory
            .getLogger(DocumentDbRestController.class);

    private static final String CR = "</BR>";

    @Autowired
    private UserRepository repository;

    @RequestMapping(value = "/docdb", method = RequestMethod.GET)
    @ResponseBody
    public String createUser(HttpServletResponse response) {
        final StringBuffer result = new StringBuffer();

        final User testUser = new User("testId", "testFirstName", "testLastName", "test address line one");

        LOG.debug("Deleting all records in repo..." + CR);
        repository.deleteAll();
        
        LOG.debug("Saving new User object...");
        repository.save(testUser);

        LOG.debug("Retrieving User object...");
        final User user = repository.findOne(testUser.getId());

        Assert.state(user.getFirstName().equals(testUser.getFirstName()), "query result firstName doesn't match!");
        Assert.state(user.getLastName().equals(testUser.getLastName()), "query result lastName doesn't match!");

        LOG.debug("UserRepository.findOne() result: " + user.toString());
        result.append("UserRepository.findOne() result: " + user.toString() + CR);
        
        return result.toString();
    }
}
