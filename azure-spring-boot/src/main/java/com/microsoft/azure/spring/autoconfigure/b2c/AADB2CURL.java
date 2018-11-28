/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.b2c;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AADB2CURL {

    // 'p' is the abbreviation for 'policy'.
    private static final String OPENID_AUTHORIZE_PATTERN =
            "https://%s.b2clogin.com/%s.onmicrosoft.com/oauth2/v2.0/authorize?" +
                    "client_id=%s&" +
                    "redirect_uri=%s&" +
                    "response_mode=query&" +
                    "response_type=code+id_token&" +
                    "scope=openid&" +
                    "state=%s&" +
                    "nonce=%s&" +
                    "p=%s";

    private static final String OPENID_LOGOUT_PATTERN =
            "https://%s.b2clogin.com/%s.onmicrosoft.com/oauth2/v2.0/logout?" +
                    "post_logout_redirect_uri=%s&" +
                    "p=%s";

    private static final String OPENID_CONFIGURATION_PATTERN =
            "https://%s.b2clogin.com/%s.onmicrosoft.com/v2.0/.well-known/openid-configuration?" +
                    "p=%s";

    private static final String OPENID_PASSWORD_RESET_PATTERN = OPENID_AUTHORIZE_PATTERN;

    private static final String OPENID_PROFILE_EDIT_PATTERN = OPENID_AUTHORIZE_PATTERN;

    public static final String ATTRIBUTE_NONCE = "nonce";

    public static final String ATTRIBUTE_STATE = "state";

    private static String getUUID() {
        return UUID.randomUUID().toString();
    }

    private static String getEncodedURL(@NonNull String url) {
        try {
            return URLEncoder.encode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new AADB2CConfigurationException("failed to encode url: " + url, e);
        }
    }

    public static String toAbsoluteURL(String url, @NonNull HttpServletRequest request) {
        try {
            new URL(url);
            return url;
        } catch (MalformedURLException e) {
            throw new AADB2CConfigurationException("Invalid URL: " + url, e);
// TODO(pan): need to investigate the relative URL with context path of spring security, only support absolute URL.
//            try {
//                new URI(url);
//                final URL requestURL = new URL(request.getRequestURL().toString());
//            URI format =>scheme:[//authority]path[?query][#fragment]
//                return String.format("%s:%s%s",
//                        requestURL.getProtocol(),
//                        StringUtils.hasText(requestURL.getAuthority()) ? "//" + requestURL.getAuthority() : "",
//                        url.startsWith("/") ? url : "/" + url
//                );
//            } catch (URISyntaxException | MalformedURLException e) {
//                throw new AADB2CConfigurationException("Invalid URL: " + url, e);
//            }
        }
    }

    /**
     * Take state's format as UUID-RequestURI when redirect to sign-in URL.
     *
     * @param requestURL from ${@link HttpServletRequest} that user attempt to access.
     * @return the encoded state String.
     */
    private static String getState(HttpServletRequest request, String requestURL) {
        final String state = toAbsoluteURL(requestURL, request);

        request.getSession().setAttribute(ATTRIBUTE_STATE, state);

        return state;
    }

    private static String getNonce(HttpServletRequest request) {
        final String nonce = getUUID();

        request.getSession().setAttribute(ATTRIBUTE_NONCE, nonce);

        return nonce;
    }

    private static String getOpenIdAuthorizeURL(AADB2CProperties properties, AADB2CProperties.Policy policy,
                                                String redirectURL, HttpServletRequest request) {
        final String nonce = getNonce(request);
        final String state = getState(request, redirectURL);

        return String.format(OPENID_AUTHORIZE_PATTERN,
                properties.getTenant(),
                properties.getTenant(),
                properties.getClientId(),
                getEncodedURL(toAbsoluteURL(policy.getReplyURL(), request)),
                state,
                nonce,
                policy.getName()
        );
    }

    /**
     * Get the openid sign up or sign in URL based on ${@link AADB2CProperties}, within redirectURL in state field.
     *
     * @param properties  of ${@link AADB2CProperties}.
     * @param redirectURL from ${@link HttpServletRequest} that user attempt to access.
     * @return the URL of openid sign up or sign in.
     */
    public static String getOpenIdSignUpOrSignInURL(@NonNull AADB2CProperties properties, String redirectURL,
                                                    @NonNull HttpServletRequest request) {
        return getOpenIdAuthorizeURL(properties, properties.getPolicies().getSignUpOrSignIn(), redirectURL, request);
    }

    /**
     * Get the openid sign up or sign in URL based on ${@link AADB2CProperties}, within redirectURL in state field.
     *
     * @param properties  of ${@link AADB2CProperties}.
     * @param redirectURL from ${@link HttpServletRequest} that user attempt to access.
     * @param request     from ${@link HttpServletRequest}.
     * @return the URL of openid password reset.
     */
    public static String getOpenIdPasswordResetURL(@NonNull AADB2CProperties properties, String redirectURL,
                                                   @NonNull HttpServletRequest request) {
        return getOpenIdAuthorizeURL(properties, properties.getPolicies().getPasswordReset(), redirectURL, request);
    }

    /**
     * Get the openid profile edit URL based on ${@link AADB2CProperties}, within redirectURL in state field.
     *
     * @param properties  of ${@link AADB2CProperties}.
     * @param redirectURL from ${@link HttpServletRequest} that user attempt to access.
     * @param request     from ${@link HttpServletRequest}.
     * @return the URL of openid profile edit.
     */
    public static String getOpenIdProfileEditURL(@NonNull AADB2CProperties properties, String redirectURL,
                                                 @NonNull HttpServletRequest request) {
        return getOpenIdAuthorizeURL(properties, properties.getPolicies().getProfileEdit(), redirectURL, request);
    }

    /**
     * Get the openid logout URL based on ${@link AADB2CProperties}.
     *
     * @param properties of ${@link AADB2CProperties}.
     * @return the URL of openid logout.
     */
    public static String getOpenIdLogoutURL(@NonNull AADB2CProperties properties, @NonNull HttpServletRequest request) {
        return String.format(OPENID_LOGOUT_PATTERN,
                properties.getTenant(),
                properties.getTenant(),
                getEncodedURL(toAbsoluteURL(properties.getLogoutSuccessUrl(), request)),
                properties.getPolicies().getSignUpOrSignIn().getName()
        );
    }

    /**
     * Get the openid configuration URL based on ${@link AADB2CProperties}.
     *
     * @param properties of ${@link AADB2CProperties}.
     * @return the URL of openid configuration.
     */
    public static String getOpenIdSignUpOrInConfigurationURL(@NonNull AADB2CProperties properties) {
        return String.format(OPENID_CONFIGURATION_PATTERN,
                properties.getTenant(),
                properties.getTenant(),
                properties.getPolicies().getSignUpOrSignIn().getName()
        );
    }
}
