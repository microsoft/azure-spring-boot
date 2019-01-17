/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.autoconfigure.aad;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Custom validator to validate that One of the two UserGroup properties are not empty
 */
public class AADUserGroupsPropertyValidator
        implements ConstraintValidator<ValidUserGroupProperties, AADAuthenticationProperties> {

    @Override
    public boolean isValid(AADAuthenticationProperties aadAuthenticationProperties,
            ConstraintValidatorContext context) {
        return !aadAuthenticationProperties.getActiveDirectoryGroups().isEmpty() || !aadAuthenticationProperties
                .getUserGroup().getAllowedGroups().isEmpty();
    }
}
