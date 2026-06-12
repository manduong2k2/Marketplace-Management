package com.Marketplace_Management.Shared.Application.Annotation.Rules;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

import com.Marketplace_Management.Shared.Application.Validator.Rules.UniqueValidator;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueValidator.class)
@Repeatable(UniqueList.class)
public @interface Unique {

    boolean when() default true;

    String message() default "Value already exists";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String table();
    String column();
    Class<?> type() default String.class;

    String deletedAtColumn() default "";
    
    String whereClause() default "";
}
