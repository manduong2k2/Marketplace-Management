package com.StoreManagement.Shared.Application.Validator.Rules;

import com.StoreManagement.Shared.Application.Annotation.Rules.Distinct;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DistinctValidator implements ConstraintValidator<Distinct, List<?>> {
    @Override
    public boolean isValid(List<?> value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }

        Set<Object> set = new HashSet<>();

        for (Object item : value) {
            if (!set.add(item)) {
                return false; // duplicate found
            }
        }

        return true;
    }
}