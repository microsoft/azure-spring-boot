/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.msgraph.sample;

import com.microsoft.azure.msgraph.sample.custom.Contacts;
import com.microsoft.azure.msgraph.api.Microsoft;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Controller
public class HelloController {
    private Microsoft microsoft;
    private ConnectionRepository connectionRepository;

    public HelloController(Microsoft microsoft, ConnectionRepository connectionRepository) {
        this.microsoft = microsoft;
        this.connectionRepository = connectionRepository;
    }

    @RequestMapping("/")
    public String helloMicrosoft(Model model) {
        if (connectionRepository.findPrimaryConnection(Microsoft.class) == null) {
            return "redirect:/connect/microsoft";
        }

        model.addAttribute("user", microsoft.userOperations().getUserProfile());

        return "hello";
    }

    @RequestMapping("/messages")
    public String getMessages(Model model) {
        if (connectionRepository.findPrimaryConnection(Microsoft.class) == null) {
            return "redirect:/connect/microsoft";
        }

        model.addAttribute("messages", microsoft.mailOperations().listMessages().getValue());

        return "messages";
    }

    @RequestMapping("/contacts")
    public String getContacts(Model model) {
        if (connectionRepository.findPrimaryConnection(Microsoft.class) == null) {
            return "redirect:/connect/microsoft";
        }

        final RestTemplate restTemplate = microsoft.customOperations().getRestTemplate();
        final URI uri = microsoft.customOperations().getGraphAPIURI("me/contacts");
        final Contacts contacts = restTemplate.getForObject(uri, Contacts.class);
        model.addAttribute("contacts", contacts.getContacts());

        return "contacts";
    }
}
