package com.StoreManagement.Shared.Application.Annotation.Rules;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

import com.StoreManagement.Shared.Application.Validator.Rules.DistinctValidator;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DistinctValidator.class)
public @interface Distinct {
    String message() default "List contains duplicated values";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}