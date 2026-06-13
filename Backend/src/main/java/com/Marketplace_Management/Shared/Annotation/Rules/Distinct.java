package com.Marketplace_Management.Shared.Annotation.Rules;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

import com.Marketplace_Management.Shared.Validation.Rules.DistinctValidator;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DistinctValidator.class)
public @interface Distinct {
    String message() default "List contains duplicated values";

    boolean when() default true;

    Class<?>[] groups() default {};

    String field() default "";

    Class<? extends Payload>[] payload() default {};
}