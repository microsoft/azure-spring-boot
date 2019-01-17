/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.aad;

import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class AADUserGroupsPropertyValidatorTest {

    private AADUserGroupsPropertyValidator aadUserGroupsPropertyValidator;
    private AADAuthenticationProperties aadAuthenticationProperties;
    private Validator validator;

    @Before
    public void setUp() {
        aadUserGroupsPropertyValidator = new AADUserGroupsPropertyValidator();
        aadAuthenticationProperties = new AADAuthenticationProperties();
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void isValidNoGroupsDefined() {
        assertThat(aadUserGroupsPropertyValidator.isValid(aadAuthenticationProperties, null)).isFalse();
    }

    @Test
    public void isValidThrowsValidationException() {
        final Set<ConstraintViolation<AADAuthenticationProperties>> violations = validator
                .validate(aadAuthenticationProperties);
        assertThat(violations).isNotEmpty().hasSize(1).extracting(ConstraintViolation::getMessage)
                .containsExactly("azure.activedirectory.user-group.allowed-groups cannot be empty.");
    }

    @Test
    public void isValidDeprecatedPropertySet() {
        aadAuthenticationProperties.setActiveDirectoryGroups(Collections.singletonList("user-group"));
        assertThat(aadUserGroupsPropertyValidator.isValid(aadAuthenticationProperties, null)).isTrue();
    }

    @Test
    public void isValidUserGroupPropertySet() {
        aadAuthenticationProperties.getUserGroup().setAllowedGroups(Collections.singletonList("user-group"));
        assertThat(aadUserGroupsPropertyValidator.isValid(aadAuthenticationProperties, null)).isTrue();
    }

    @Test
    public void isValidBothUserGroupPropertiesSet() {
        aadAuthenticationProperties.setActiveDirectoryGroups(Collections.singletonList("user-group"));
        aadAuthenticationProperties.getUserGroup().setAllowedGroups(Collections.singletonList("user-group"));
        assertThat(aadUserGroupsPropertyValidator.isValid(aadAuthenticationProperties, null)).isTrue();
    }
}
