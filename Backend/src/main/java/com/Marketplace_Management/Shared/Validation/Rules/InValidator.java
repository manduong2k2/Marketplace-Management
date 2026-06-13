package com.Marketplace_Management.Shared.Validation.Rules;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import com.Marketplace_Management.Shared.Annotation.Rules.In;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class InValidator implements ConstraintValidator<In, String>{
    private Set<String> values;
    private boolean when;

    @Override
    public void initialize(In annotation) {
        values = Arrays.stream(annotation.enumClass().getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toSet());
        when = annotation.when();
    }

    @Override
    public boolean isValid(String value,
                           ConstraintValidatorContext context) {
        
        if(!when) {
            return true;
        }
        
        return value == null || values.contains(value);
    }
}
