/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package sample.aad.b2c.controller;

import com.microsoft.azure.spring.autoconfigure.b2c.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class WebController {

    private void initializeModel(Model model, PreAuthenticatedAuthenticationToken token) {
        if (token != null) {
            final UserPrincipal principal = (UserPrincipal) token.getPrincipal();

            model.addAttribute("name", principal.getDisplayName());
            model.addAttribute("email", principal.getEmails().get(0));
            model.addAttribute("city", principal.getCity());
            model.addAttribute("country", principal.getCountryOrRegion());
            model.addAttribute("idProvider", principal.getIdentityProvider());
            model.addAttribute("authenticated", "true");
        } else {
            model.addAttribute("authenticated", "false");
        }
    }

    @GetMapping(value = "/")
    public String index(Model model, PreAuthenticatedAuthenticationToken token) {
        initializeModel(model, token);

        return "index";
    }

    @GetMapping(value = "/greeting")
    public String greeting(Model model, PreAuthenticatedAuthenticationToken token) {
        initializeModel(model, token);

        return "greeting";
    }

    @GetMapping(value = "/home")
    public String home(Model model, PreAuthenticatedAuthenticationToken token) {
        initializeModel(model, token);

        return "home";
    }
}
