/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.aad;

import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.ClientCredential;
import com.microsoft.aad.adal4j.UserAssertion;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AADAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(AADAuthenticationFilter.class);

    private static final String CURRENT_USER_PRINCIPAL = "CURRENT_USER_PRINCIPAL";
    private static final String CURRENT_USER_PRINCIPAL_GRAPHAPI_TOKEN = "CURRENT_USER_PRINCIPAL_GRAPHAPI_TOKEN";

    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_TYPE = "Bearer ";

    private AADAuthenticationFilterProperties aadAuthFilterProp;
    private ServiceEndpointsProperties serviceEndpointsProp;

    public AADAuthenticationFilter(AADAuthenticationFilterProperties aadAuthFilterProp,
                                   ServiceEndpointsProperties serviceEndpointsProp) {
        this.aadAuthFilterProp = aadAuthFilterProp;
        this.serviceEndpointsProp = serviceEndpointsProp;
    }

    private AuthenticationResult acquireTokenForGraphApi(
            String idToken,
            String tenantId,
            ServiceEndpoints serviceEndpoints)
            throws MalformedURLException, ServiceUnavailableException, InterruptedException, ExecutionException {

        final ClientCredential credential = new ClientCredential(
                aadAuthFilterProp.getClientId(), aadAuthFilterProp.getClientSecret());
        final UserAssertion assertion = new UserAssertion(idToken);

        AuthenticationResult result = null;
        ExecutorService service = null;
        try {
            service = Executors.newFixedThreadPool(1);
            final AuthenticationContext context = new AuthenticationContext(
                    serviceEndpoints.getAadSigninUri() + tenantId + "/", true, service);
            final Future<AuthenticationResult> future = context
                    .acquireToken(serviceEndpoints.getAadGraphApiUri(), assertion, credential, null);
            result = future.get();
        } finally {
            if (service != null) {
                service.shutdown();
            }
        }

        if (result == null) {
            throw new ServiceUnavailableException(
                    "unable to acquire on-behalf-of token for client " + aadAuthFilterProp.getClientId());
        }
        return result;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader(TOKEN_HEADER);

        if (authHeader != null && authHeader.startsWith(TOKEN_TYPE)) {
            try {
                final String idToken = authHeader.replace(TOKEN_TYPE, "");
                UserPrincipal principal = (UserPrincipal) request
                        .getSession().getAttribute(CURRENT_USER_PRINCIPAL);
                String graphApiToken = (String) request
                        .getSession().getAttribute(CURRENT_USER_PRINCIPAL_GRAPHAPI_TOKEN);

                final ServiceEndpoints serviceEndpoints =
                        serviceEndpointsProp.getServiceEndpoints(aadAuthFilterProp.getEnvironment());

                if (principal == null || graphApiToken == null || graphApiToken.isEmpty()) {
                    principal = new UserPrincipal(idToken, serviceEndpoints);
                    graphApiToken = acquireTokenForGraphApi(
                            idToken, principal.getClaim().toString(), serviceEndpoints).getAccessToken();
                    request.getSession().setAttribute(CURRENT_USER_PRINCIPAL, principal);
                    request.getSession().setAttribute(CURRENT_USER_PRINCIPAL_GRAPHAPI_TOKEN, graphApiToken);
                }

                final Authentication authentication = new
                        PreAuthenticatedAuthenticationToken(
                        principal, null,
                        principal.getAuthoritiesByUserGroups(
                                principal.getGroups(graphApiToken),
                                aadAuthFilterProp.getActiveDirectoryGroups()));
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
