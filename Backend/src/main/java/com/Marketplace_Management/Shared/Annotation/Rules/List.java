package com.Marketplace_Management.Shared.Annotation.Rules;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

import com.Marketplace_Management.Shared.Validation.Rules.ListValidator;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ListValidator.class)
public @interface List {

    boolean when() default true;

    String[] available() default {};

    String message() default "Invalid value in list";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
