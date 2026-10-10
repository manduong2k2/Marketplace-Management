package com.Marketplace_Management.Shared;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.Marketplace_Management.Shared.Annotation.Rules.Uuid;
import com.Marketplace_Management.Shared.Utils.Helpers.UuidV7;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class UuidConstraintTest {
    private static ValidatorFactory factory;
    private static Validator validator;

    record Single(@Uuid String id) {}
    record Many(List<@Uuid String> ids) {}

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    private boolean valid(String id) {
        return validator.validate(new Single(id)).isEmpty();
    }

    @Test
    void acceptsV7_andLegacyV4() {
        assertTrue(valid(UuidV7.generate().toString()));
        assertTrue(valid(UUID.randomUUID().toString()));
        assertTrue(valid(UuidV7.generate().toString().toUpperCase()));
        assertTrue(valid(null)); // optional unless combined with @NotNull
    }

    @Test
    void rejectsOtherVersions_nil_andGarbage() {
        assertEquals(false, valid("6ba7b810-9dad-11d1-80b4-00c04fd430c8")); // v1
        assertEquals(false, valid("00000000-0000-0000-0000-000000000000")); // nil
        assertEquals(false, valid("not-a-uuid"));
        assertEquals("Must be a valid id", validator.validate(new Single("x")).iterator().next().getMessage());
    }

    @Test
    void worksOnListElements() {
        assertTrue(validator.validate(new Many(List.of(UuidV7.generate().toString()))).isEmpty());
        assertEquals(1, validator.validate(new Many(List.of(UuidV7.generate().toString(), "oops"))).size());
    }
}
