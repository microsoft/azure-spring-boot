/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package sample.microsoft.graph;

import com.microsoft.azure.msgraph.api.Microsoft;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HelloController {

    private ConnectionRepository connectionRepository;

    public HelloController(ConnectionRepository connectionRepository) {
        this.connectionRepository = connectionRepository;
    }

    @GetMapping
    public String helloFacebook(Model model) {
        if (connectionRepository.findPrimaryConnection(Microsoft.class) == null) {
            return "redirect:/connect/microsoft";
        }

        final Connection<Microsoft> connection = connectionRepository
                .findPrimaryConnection(Microsoft.class);

        model.addAttribute("myProfile", connection.getApi().meOperations().getMyProfile());

        return "hello";
    }

}
