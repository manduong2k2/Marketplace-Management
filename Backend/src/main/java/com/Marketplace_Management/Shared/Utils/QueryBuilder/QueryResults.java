package com.Marketplace_Management.Shared.Utils.QueryBuilder;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

/**
 * QueryBuilder returns raw JDBC values. Converts date/time values to LocalDateTime
 * so they map cleanly onto response DTOs (and serialize as ISO strings).
 */
public final class QueryResults {
    private QueryResults() {}

    @SuppressWarnings("unchecked")
    public static Map<String, Object> normalize(Map<String, Object> row) {
        row.replaceAll((key, value) -> {
            if (value instanceof java.sql.Timestamp timestamp) {
                return timestamp.toLocalDateTime();
            }
            if (value instanceof OffsetDateTime offsetDateTime) {
                return offsetDateTime.toLocalDateTime();
            }
            if (value instanceof Map<?, ?> child) {
                return normalize((Map<String, Object>) child);
            }
            if (value instanceof List<?> list) {
                list.forEach(element -> {
                    if (element instanceof Map<?, ?> child) {
                        normalize((Map<String, Object>) child);
                    }
                });
            }
            return value;
        });
        return row;
    }
}
