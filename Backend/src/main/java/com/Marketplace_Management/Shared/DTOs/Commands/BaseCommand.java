package com.Marketplace_Management.Shared.DTOs.Commands;

public abstract class BaseCommand {
    protected static String safeTrim(String value) {
        return value == null ? null : value.trim();
    }
}
