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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.naming.ServiceUnavailableException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AzureADJwtTokenFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(AzureADJwtTokenFilter.class);

    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_TYPE = "Bearer ";

    private AzureADJwtFilterProperties aadJwtFilterProp;

    public AzureADJwtTokenFilter(AzureADJwtFilterProperties aadJwtFilterProp) {
        this.aadJwtFilterProp = aadJwtFilterProp;
    }

    private AuthenticationResult acquireTokenForGraphApi(
            String tokenEncoded,
            String tenantId) throws Throwable {
        final ClientCredential credential = new ClientCredential(
                aadJwtFilterProp.getClientId(), aadJwtFilterProp.getClientSecret());
        final ClientAssertion assertion = new ClientAssertion(tokenEncoded);

        AuthenticationResult result = null;
        ExecutorService service = null;
        try {
            service = Executors.newFixedThreadPool(1);
            final AuthenticationContext context = new AuthenticationContext(
                    aadJwtFilterProp.getAadSignInUri() + tenantId + "/",
                    true,
                    service);
            final Future<AuthenticationResult> future = context
                    .acquireToken(aadJwtFilterProp.getAadGraphAPIUri(), assertion, credential, null);
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
                    "unable to acquire on-behalf-of token for client " + aadJwtFilterProp.getClientId());
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
                final String tokenEncoded = authHeader.replace(TOKEN_TYPE, "");
                final AzureADJwtToken jwtToken = new AzureADJwtToken(tokenEncoded);

                AzureADUserMembership userProfile;
                final List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
                try {
                    final String tid = jwtToken.getClaim("tid").toString();

                    final AuthenticationResult result = acquireTokenForGraphApi(
                            tokenEncoded,
                            tid);
                    userProfile = new AzureADUserMembership(result.getAccessToken());

                    if (CustomPermissionEvaluator.hasPermission(
                            userProfile.getUserMemberships(), aadJwtFilterProp.getAllowedRolesGroups())) {
                        authorities.add(new SimpleGrantedAuthority("ROLE_ALLOWED"));
                    } else {
                        authorities.add(new SimpleGrantedAuthority("ROLE_DISALLOWED"));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } catch (Throwable throwable) {
                    throw new RuntimeException(throwable);
                }
                final Authentication authentication = new
                        PreAuthenticatedAuthenticationToken(jwtToken, null, authorities);
                authentication.setAuthenticated(true);
                log.info("Request token verification success. {0}", authentication);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        filterChain.doFilter(request, response);
    }

}
