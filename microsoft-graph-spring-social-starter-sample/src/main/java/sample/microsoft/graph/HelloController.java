/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package sample.microsoft.graph;

import com.microsoft.azure.msgraph.api.Microsoft;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HelloController {

    private Microsoft microsoft;
    private ConnectionRepository connectionRepository;

    public HelloController(Microsoft microsoft, ConnectionRepository connectionRepository) {
        this.microsoft = microsoft;
        this.connectionRepository = connectionRepository;
    }

    @GetMapping
    public String helloFacebook(Model model) {
        if (connectionRepository.findPrimaryConnection(Microsoft.class) == null) {
            return "redirect:/connect/microsoft";
        }

        model.addAttribute("user", microsoft.meOperations().getUserProfile());

        return "hello";
    }

}
