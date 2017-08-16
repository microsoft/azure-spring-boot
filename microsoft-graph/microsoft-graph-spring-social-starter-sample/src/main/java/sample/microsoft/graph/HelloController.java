/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package sample.microsoft.graph;

import com.microsoft.azure.msgraph.api.Microsoft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import sample.microsoft.graph.custom.Contacts;

import java.net.URI;

@Controller
public class HelloController {
    private static final Logger LOG = LoggerFactory.getLogger(HelloController.class);

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
