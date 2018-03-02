/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package sample.cloudfoundry.documentdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

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
        final StringBuilder result = new StringBuilder();
        final User testUser = new User("testId", "testFirstName", "testLastName", "test address line one");

        LOG.debug("Deleting all records in repo... {}", CR);
        repository.deleteAll();

        LOG.debug("Saving new User object...");
        repository.save(testUser);

        LOG.debug("Retrieving User object...");
        final Optional<User> user = repository.findById(testUser.getId());

        Assert.state(user.isPresent(), "Can not find User.");
        Assert.state(user.get().getFirstName().equals(testUser.getFirstName()), "query FirstName unmatched!");
        Assert.state(user.get().getLastName().equals(testUser.getLastName()), "query LastName unmatched!");

        LOG.debug("UserRepository.findById() result: {}", user.toString());
        result.append("UserRepository.findById() result: " + user.toString() + CR);

        return result.toString();
    }
}
