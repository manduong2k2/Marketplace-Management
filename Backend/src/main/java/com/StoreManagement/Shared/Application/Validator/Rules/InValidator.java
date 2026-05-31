package com.StoreManagement.Shared.Application.Validator.Rules;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import com.StoreManagement.Shared.Application.Annotation.Rules.In;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class InValidator implements ConstraintValidator<In, String>{
    private Set<String> values;

    @Override
    public void initialize(In annotation) {
        values = Arrays.stream(annotation.enumClass().getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(String value,
                           ConstraintValidatorContext context) {

        return value == null || values.contains(value);
    }
}
