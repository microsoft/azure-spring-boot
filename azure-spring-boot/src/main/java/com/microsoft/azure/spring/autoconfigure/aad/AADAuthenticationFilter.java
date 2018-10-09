/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.aad;

import com.microsoft.aad.adal4j.ClientCredential;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.util.ResourceRetriever;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.naming.ServiceUnavailableException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.concurrent.ExecutionException;

public class AADAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(AADAuthenticationFilter.class);

    private static final String CURRENT_USER_PRINCIPAL = "CURRENT_USER_PRINCIPAL";
    private static final String CURRENT_USER_PRINCIPAL_GRAPHAPI_TOKEN = "CURRENT_USER_PRINCIPAL_GRAPHAPI_TOKEN";

    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_TYPE = "Bearer ";

    private AADAuthenticationProperties aadAuthProps;
    private ServiceEndpointsProperties serviceEndpointsProps;
    private ResourceRetriever resourceRetriever;
    private UserPrincipalManager principalManager;

    public AADAuthenticationFilter(AADAuthenticationProperties aadAuthProps,
                                   ServiceEndpointsProperties serviceEndpointsProps,
                                   ResourceRetriever resourceRetriever) {
        this.aadAuthProps = aadAuthProps;
        this.serviceEndpointsProps = serviceEndpointsProps;
        this.resourceRetriever = resourceRetriever;
        this.principalManager = new UserPrincipalManager(
                serviceEndpointsProps.getServiceEndpoints(aadAuthProps.getEnvironment()), resourceRetriever);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader(TOKEN_HEADER);

        if (authHeader != null && authHeader.startsWith(TOKEN_TYPE)) {
            try {
                final String idToken = authHeader.replace(TOKEN_TYPE, "");
                UserPrincipal principal = (UserPrincipal) request
                        .getSession().getAttribute(CURRENT_USER_PRINCIPAL);
                String graphApiToken = (String) request
                        .getSession().getAttribute(CURRENT_USER_PRINCIPAL_GRAPHAPI_TOKEN);

                final ClientCredential credential =
                        new ClientCredential(aadAuthProps.getClientId(), aadAuthProps.getClientSecret());

                final AzureADGraphClient client =
                        new AzureADGraphClient(credential, aadAuthProps, serviceEndpointsProps);

                if (principal == null || graphApiToken == null || graphApiToken.isEmpty()) {
                    principal = principalManager.buildUserPrincipal(idToken);

                    final String tenantId = principal.getClaim().toString();
                    graphApiToken = client.acquireTokenForGraphApi(idToken, tenantId).getAccessToken();

                    principal.setUserGroups(client.getGroups(graphApiToken));

                    request.getSession().setAttribute(CURRENT_USER_PRINCIPAL, principal);
                    request.getSession().setAttribute(CURRENT_USER_PRINCIPAL_GRAPHAPI_TOKEN, graphApiToken);
                }

                final Authentication authentication = new PreAuthenticatedAuthenticationToken(
                            principal, null, client.convertGroupsToGrantedAuthorities(principal.getUserGroups()));

                authentication.setAuthenticated(true);
                log.info("Request token verification success. {}", authentication);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (MalformedURLException | ParseException | BadJOSEException | JOSEException ex) {
                log.error("Failed to initialize UserPrincipal.", ex);
                throw new ServletException(ex);
            } catch (ServiceUnavailableException | InterruptedException | ExecutionException ex) {
                log.error("Failed to acquire graph api token.", ex);
                throw new ServletException(ex);
            }
        }

        filterChain.doFilter(request, response);
    }
}
