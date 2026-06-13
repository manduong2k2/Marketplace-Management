package com.Marketplace_Management.Shared.Validation.Rules;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.Marketplace_Management.Shared.Annotation.Rules.Distinct;

public class DistinctValidator implements ConstraintValidator<Distinct, List<?>> {

    private boolean when;
    private String field;

    @Override
    public void initialize(Distinct annotation) {
        when = annotation.when();
        field = annotation.field();
    }

    @Override
    public boolean isValid(List<?> value, ConstraintValidatorContext context) {

        if (!when || value == null || value.isEmpty()) {
            return true;
        }

        Set<Object> uniqueValues = new HashSet<>();

        try {

            for (Object item : value) {

                Object key;

                if (field == null || field.isBlank()) {
                    key = item;
                } else {
                    Field declaredField =
                            item.getClass().getDeclaredField(field);

                    declaredField.setAccessible(true);

                    key = declaredField.get(item);
                }

                if (!uniqueValues.add(normalize(key))) {
                    return false;
                }
            }

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    private Object normalize(Object value) {

        if (value instanceof List<?> list) {
            return list.stream()
                    .map(String::valueOf)
                    .sorted()
                    .toList();
        }

        return value;
    }
}