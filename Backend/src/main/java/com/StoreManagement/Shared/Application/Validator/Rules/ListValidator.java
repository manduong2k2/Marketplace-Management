package com.StoreManagement.Shared.Application.Validator.Rules;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class ListValidator implements ConstraintValidator<com.StoreManagement.Shared.Application.Annotation.Rules.List, List<String>> {

    private Set<String> allowed;
    private boolean when;

    @Override
    public void initialize(com.StoreManagement.Shared.Application.Annotation.Rules.List annotation) {
        allowed = new HashSet<>(Arrays.asList(annotation.available()));
        when = annotation.when();
    }

    @Override
    public boolean isValid(List<String> values, ConstraintValidatorContext context) {

        if(!when) {
            return true;
        }

        if (values == null) return true;

        for (String value : values) {
            if (value == null || !allowed.contains(value)) {
                return false;
            }
        }

        return true;
    }
}
