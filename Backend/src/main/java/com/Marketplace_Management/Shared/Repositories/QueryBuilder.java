package com.Marketplace_Management.Shared.Repositories;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class QueryBuilder {
    private List<String> selectColumns;
    private String fromTable;
    private boolean distinct;
    private List<JoinClause> joins;
    private List<WhereClause> wheres;
    private List<String> orderBys;
    private Integer limit;
    private Integer offset;
    private Map<String, Object> parameters;
    private int paramIndex;

    private static class JoinClause {
        String type;
        String table;
        String leftColumn;
        String rightColumn;

        JoinClause(String type, String table, String leftColumn, String rightColumn) {
            this.type = type;
            this.table = table;
            this.leftColumn = leftColumn;
            this.rightColumn = rightColumn;
        }
    }

    private static class WhereClause {
        String column;
        Operator operator;
        Object value;
        String logicalOperator; // "AND", "OR", or null for first WHERE

        WhereClause(String column, Operator operator, Object value, String logicalOperator) {
            this.column = column;
            this.operator = operator;
            this.value = value;
            this.logicalOperator = logicalOperator;
        }
    }

    public QueryBuilder() {
        this.selectColumns = new ArrayList<>();
        this.joins = new ArrayList<>();
        this.wheres = new ArrayList<>();
        this.orderBys = new ArrayList<>();
        this.parameters = new LinkedHashMap<>();
        this.paramIndex = 0;
    }

    // Query methods
    public QueryBuilder select(String... columns) {
        Collections.addAll(this.selectColumns, columns);
        return this;
    }

    public QueryBuilder select(List<String> columns) {
        this.selectColumns.addAll(columns);
        return this;
    }

    public QueryBuilder from(String table) {
        this.fromTable = table;
        return this;
    }

    public QueryBuilder distinct() {
        this.distinct = true;
        return this;
    }

    // Join methods
    public QueryBuilder join(String table, String leftColumn, String rightColumn) {
        return innerJoin(table, leftColumn, rightColumn);
    }

    public QueryBuilder leftJoin(String table, String leftColumn, String rightColumn) {
        this.joins.add(new JoinClause("LEFT JOIN", table, leftColumn, rightColumn));
        return this;
    }

    public QueryBuilder rightJoin(String table, String leftColumn, String rightColumn) {
        this.joins.add(new JoinClause("RIGHT JOIN", table, leftColumn, rightColumn));
        return this;
    }

    public QueryBuilder innerJoin(String table, String leftColumn, String rightColumn) {
        this.joins.add(new JoinClause("INNER JOIN", table, leftColumn, rightColumn));
        return this;
    }

    // Where methods
    public QueryBuilder where(String column, Operator operator, Object value) {
        this.wheres.add(new WhereClause(column, operator, value, null));
        return this;
    }

    public QueryBuilder and(String column, Operator operator, Object value) {
        this.wheres.add(new WhereClause(column, operator, value, "AND"));
        return this;
    }

    public QueryBuilder or(String column, Operator operator, Object value) {
        this.wheres.add(new WhereClause(column, operator, value, "OR"));
        return this;
    }

    // Special where conditions
    public QueryBuilder whereIn(String column, Collection<?> values) {
        this.wheres.add(new WhereClause(column, Operator.IN, values, null));
        return this;
    }

    public QueryBuilder whereNotIn(String column, Collection<?> values) {
        this.wheres.add(new WhereClause(column, Operator.NOT_IN, values, null));
        return this;
    }

    public QueryBuilder whereNull(String column) {
        this.wheres.add(new WhereClause(column, Operator.IS_NULL, null, null));
        return this;
    }

    public QueryBuilder whereNotNull(String column) {
        this.wheres.add(new WhereClause(column, Operator.IS_NOT_NULL, null, null));
        return this;
    }

    public QueryBuilder whereLike(String column, String keyword) {
        String pattern = "%" + keyword + "%";
        this.wheres.add(new WhereClause(column, Operator.LIKE, pattern, null));
        return this;
    }

    // Sort methods
    public QueryBuilder orderBy(String column) {
        this.orderBys.add(column + " ASC");
        return this;
    }

    public QueryBuilder orderByDesc(String column) {
        this.orderBys.add(column + " DESC");
        return this;
    }

    // Pagination methods
    public QueryBuilder limit(int limit) {
        this.limit = limit;
        return this;
    }

    public QueryBuilder offset(int offset) {
        this.offset = offset;
        return this;
    }

    public QueryBuilder paginate(int page, int size) {
        this.limit = size;
        this.offset = page * size;
        return this;
    }

    // Build methods
    public String toSql() {
        StringBuilder sql = new StringBuilder();

        // SELECT
        sql.append("SELECT ");
        if (distinct) {
            sql.append("DISTINCT ");
        }
        if (selectColumns.isEmpty()) {
            sql.append("*");
        } else {
            sql.append(String.join(", ", selectColumns));
        }

        // FROM
        if (fromTable != null) {
            sql.append("\nFROM ").append(fromTable);
        }

        // JOINs
        for (JoinClause join : joins) {
            sql.append("\n").append(join.type).append(" ").append(join.table)
               .append("\n    ON ").append(join.leftColumn).append(" = ").append(join.rightColumn);
        }

        // WHERE
        if (!wheres.isEmpty()) {
            sql.append("\nWHERE");
            for (WhereClause where : wheres) {
                if (where.logicalOperator != null) {
                    sql.append("\n  ").append(where.logicalOperator);
                }
                sql.append(" ").append(buildWhereCondition(where));
            }
        }

        // ORDER BY
        if (!orderBys.isEmpty()) {
            sql.append("\nORDER BY ").append(String.join(", ", orderBys));
        }

        // LIMIT and OFFSET
        if (limit != null) {
            sql.append("\nLIMIT ?");
            addParameter(limit);
        }
        if (offset != null) {
            sql.append("\nOFFSET ?");
            addParameter(offset);
        }

        return sql.toString();
    }

    private String buildWhereCondition(WhereClause where) {
        StringBuilder condition = new StringBuilder();
        
        switch (where.operator) {
            case IS_NULL:
                condition.append(where.column).append(" IS NULL");
                break;
            case IS_NOT_NULL:
                condition.append(where.column).append(" IS NOT NULL");
                break;
            case IN:
            case NOT_IN:
                condition.append(where.column).append(" ").append(where.operator.getSql()).append(" (");
                if (where.value instanceof Collection) {
                    Collection<?> values = (Collection<?>) where.value;
                    List<String> placeholders = new ArrayList<>();
                    for (Object val : values) {
                        placeholders.add("?");
                        addParameter(val);
                    }
                    condition.append(String.join(", ", placeholders));
                }
                condition.append(")");
                break;
            default:
                condition.append(where.column).append(" ").append(where.operator.getSql()).append(" ?");
                addParameter(where.value);
                break;
        }
        
        return condition.toString();
    }

    private void addParameter(Object value) {
        String paramName = "param" + (++paramIndex);
        parameters.put(paramName, value);
    }

    public Map<String, Object> getParameters() {
        return new LinkedHashMap<>(parameters);
    }

    public List<LinkedHashMap<String, Object>> execute(DataSource dataSource) throws SQLException {
        return execute(dataSource.getConnection());
    }

    public List<LinkedHashMap<String, Object>> execute(Connection connection) throws SQLException {
        String sql = toSql();
        Map<String, Object> params = getParameters();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // Bind parameters
            int paramIndex = 1;
            for (Object value : params.values()) {
                stmt.setObject(paramIndex++, value);
            }

            // Execute query
            try (ResultSet rs = stmt.executeQuery()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                List<LinkedHashMap<String, Object>> results = new ArrayList<>();

                while (rs.next()) {
                    LinkedHashMap<String, Object> row = new LinkedHashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnLabel(i);
                        Object value = rs.getObject(i);
                        row.put(columnName, value);
                    }
                    results.add(row);
                }

                return results;
            }
        }
    }
}
