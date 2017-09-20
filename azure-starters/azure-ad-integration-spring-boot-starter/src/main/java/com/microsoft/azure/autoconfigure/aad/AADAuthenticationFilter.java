/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.autoconfigure.aad;

import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.ClientAssertion;
import com.microsoft.aad.adal4j.ClientCredential;
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

    public AADAuthenticationFilter(AADAuthenticationFilterProperties aadAuthFilterProp) {
        this.aadAuthFilterProp = aadAuthFilterProp;
    }

    private AuthenticationResult acquireTokenForGraphApi(
            String idToken,
            String tenantId) throws Throwable {
        final ClientCredential credential = new ClientCredential(
                aadAuthFilterProp.getClientId(), aadAuthFilterProp.getClientSecret());
        final ClientAssertion assertion = new ClientAssertion(idToken);

        AuthenticationResult result = null;
        ExecutorService service = null;
        try {
            service = Executors.newFixedThreadPool(1);
            final AuthenticationContext context = new AuthenticationContext(
                    aadAuthFilterProp.getAadSignInUri() + tenantId + "/",
                    true,
                    service);
            final Future<AuthenticationResult> future = context
                    .acquireToken(aadAuthFilterProp.getAadGraphAPIUri(), assertion, credential, null);
            result = future.get();
        } catch (ExecutionException e) {
            throw e.getCause();
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
                if (principal == null || graphApiToken == null || graphApiToken.isEmpty()) {
                    principal = new UserPrincipal(idToken);
                    graphApiToken = acquireTokenForGraphApi(
                            idToken, principal.getClaim().toString()).getAccessToken();
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
                log.info("Request token verification success. {0}", authentication);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }

        filterChain.doFilter(request, response);
    }
}
