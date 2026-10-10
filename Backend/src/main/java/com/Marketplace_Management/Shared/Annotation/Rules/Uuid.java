package com.Marketplace_Management.Shared.Annotation.Rules;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;

/**
 * An id sent as a string: a UUID of a version this system generates. New ids are v7
 * (time-ordered); rows created before the switch keep their v4 ids, so both are accepted.
 * Hibernate Validator's own @UUID only allows versions 1-5 by default, which rejects v7.
 * The nil UUID (all zeros) is never a real id. null is valid: combine with @NotNull when required.
 */
@Documented
@Target({ ElementType.FIELD, ElementType.TYPE_USE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@ReportAsSingleViolation
@org.hibernate.validator.constraints.UUID(
        version = { 4, 7 },
        allowNil = false,
        letterCase = org.hibernate.validator.constraints.UUID.LetterCase.INSENSITIVE)
public @interface Uuid {
    String message() default "Must be a valid id";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
