package com.Marketplace_Management.Shared.Repositories;

public enum Operator {
    EQ("="),
    NE("!="),
    GT(">"),
    GTE(">="),
    LT("<"),
    LTE("<="),
    LIKE("LIKE"),
    IN("IN"),
    NOT_IN("NOT IN"),
    IS_NULL("IS NULL"),
    IS_NOT_NULL("IS NOT NULL");

    private final String sql;

    Operator(String sql) {
        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }
}
