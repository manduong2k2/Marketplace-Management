package com.Marketplace_Management.Shared.Annotation.Rules;

import java.lang.annotation.*;

import com.Marketplace_Management.Shared.Validation.Rules.InValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = InValidator.class)
public @interface In {
    Class<? extends Enum<?>> enumClass();

    String message() default "Invalid enum value";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
    
    boolean when() default true;
}
