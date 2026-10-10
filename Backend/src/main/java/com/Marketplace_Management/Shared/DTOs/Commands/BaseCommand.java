package com.Marketplace_Management.Shared.DTOs.Commands;

import java.util.UUID;

import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/** Normalisation helpers for building commands from request DTOs. */
@SuperBuilder
@NoArgsConstructor
public abstract class BaseCommand {
    protected static String safeTrim(String value) {
        return value == null ? null : value.trim();
    }

    /** Trimmed value, or null when it is null or blank (an omitted optional field). */
    protected static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    /** UUID parsed from an optional string field (already validated by the request). */
    protected static UUID uuidOrNull(String value) {
        return value == null || value.isBlank() ? null : UUID.fromString(value.trim());
    }
}
