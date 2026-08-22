package com.Marketplace_Management.Shared.Repositories.QueryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class WhereBuilder<T> {

    private final EntityMetadata<T> metadata;

    private final List<String> conditions = new ArrayList<>();

    public WhereBuilder(EntityMetadata<T> metadata) {
        this.metadata = metadata;
    }

    public WhereBuilder<T> eq(
            String column,
            Object value) {

        validateColumn(column);

        conditions.add(
                "{alias}.%s = %s".formatted(
                        column,
                        formatValue(value)
                )
        );

        return this;
    }

    public WhereBuilder<T> ne(
            String column,
            Object value) {

        validateColumn(column);

        conditions.add(
                "{alias}.%s <> %s".formatted(
                        column,
                        formatValue(value)
                )
        );

        return this;
    }

    public WhereBuilder<T> gt(
            String column,
            Object value) {

        validateColumn(column);

        conditions.add(
                "{alias}.%s > %s".formatted(
                        column,
                        formatValue(value)
                )
        );

        return this;
    }

    public WhereBuilder<T> ge(
            String column,
            Object value) {

        validateColumn(column);

        conditions.add(
                "{alias}.%s >= %s".formatted(
                        column,
                        formatValue(value)
                )
        );

        return this;
    }

    public WhereBuilder<T> lt(
            String column,
            Object value) {

        validateColumn(column);

        conditions.add(
                "{alias}.%s < %s".formatted(
                        column,
                        formatValue(value)
                )
        );

        return this;
    }

    public WhereBuilder<T> le(
            String column,
            Object value) {

        validateColumn(column);

        conditions.add(
                "{alias}.%s <= %s".formatted(
                        column,
                        formatValue(value)
                )
        );

        return this;
    }

    public WhereBuilder<T> like(
            String column,
            String value) {

        validateColumn(column);

        conditions.add(
                "{alias}.%s LIKE %s".formatted(
                        column,
                        formatValue(value)
                )
        );

        return this;
    }

    public WhereBuilder<T> isNull(
            String column) {

        validateColumn(column);

        conditions.add(
                "{alias}.%s IS NULL".formatted(column)
        );

        return this;
    }

    public WhereBuilder<T> isNotNull(
            String column) {

        validateColumn(column);

        conditions.add(
                "{alias}.%s IS NOT NULL".formatted(column)
        );

        return this;
    }

    public WhereBuilder<T> in(
            String column,
            List<?> values) {

        validateColumn(column);

        if (values == null || values.isEmpty()) {

            throw new IllegalArgumentException(
                    "IN values cannot be empty"
            );
        }

        String formattedValues =
                values.stream()
                        .map(this::formatValue)
                        .collect(
                                Collectors.joining(", ")
                        );

        conditions.add(
                "{alias}.%s IN (%s)".formatted(
                        column,
                        formattedValues
                )
        );

        return this;
    }

    public WhereBuilder<T> notIn(
            String column,
            List<?> values) {

        validateColumn(column);

        if (values == null || values.isEmpty()) {

            throw new IllegalArgumentException(
                    "NOT IN values cannot be empty"
            );
        }

        String formattedValues =
                values.stream()
                        .map(this::formatValue)
                        .collect(
                                Collectors.joining(", ")
                        );

        conditions.add(
                "{alias}.%s NOT IN (%s)".formatted(
                        column,
                        formattedValues
                )
        );

        return this;
    }

    /**
     * Raw condition.
     *
     * Example:
     *
     * .and("LOWER({alias}.name) LIKE '%phone%'")
     */
    public WhereBuilder<T> and(
            String condition) {

        if (condition != null
                && !condition.isBlank()) {

            conditions.add(condition);
        }

        return this;
    }

    /**
     * Build condition với alias thực tế.
     */
    public String build(String alias) {

        return conditions.stream()
                .map(condition ->
                        condition.replace(
                                "{alias}",
                                alias
                        )
                )
                .collect(
                        Collectors.joining(
                                "\nAND "
                        )
                );
    }

    public boolean isEmpty() {
        return conditions.isEmpty();
    }

    private void validateColumn(
            String column) {

        metadata.validateColumns(column);
    }

    private String formatValue(
            Object value) {

        if (value == null) {
            return "NULL";
        }

        if (value instanceof Number) {
            return value.toString();
        }

        if (value instanceof Boolean) {
            return value.toString();
        }

        if (value instanceof UUID) {

            return "'"
                    + value
                    + "'";
        }

        if (value instanceof Enum<?> enumValue) {

            return "'"
                    + enumValue.name()
                    + "'";
        }

        return "'"
                + value
                    .toString()
                    .replace("'", "''")
                + "'";
    }
}