package com.Marketplace_Management.Shared.Repositories.QueryBuilder;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import tools.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class QueryBuilder<T> {

        private final EntityMetadata<T> metadata;

        private List<String> columns;
        private List<ChildBuilder<?>> children;

        private EntityManager entityManager;
        private ObjectMapper objectMapper;

        public QueryBuilder(Class<T> entityClass) {
                this.metadata = new EntityMetadata<>(entityClass);
        }

        public static <T> QueryBuilder<T> query(Class<T> entityClass) {
                return new QueryBuilder<>(entityClass);
        }

        public QueryBuilder<T> select(String... columns) {

                metadata.validateColumns(columns);

                this.columns = List.of(columns);

                return this;
        }

        public QueryBuilder<T> with(
                        List<ChildBuilder<?>> children) {

                this.children = children;

                return this;
        }

        public QueryBuilder<T> entityManager(
                        EntityManager entityManager) {

                this.entityManager = entityManager;

                return this;
        }

        public QueryBuilder<T> objectMapper(
                        ObjectMapper objectMapper) {

                this.objectMapper = objectMapper;

                return this;
        }

        public String build() {
                return buildRootQuery();
        }

        private String buildRootQuery() {

                StringBuilder sql = new StringBuilder("SELECT ");

                sql.append(buildRootSelect());

                if (children != null) {

                        for (ChildBuilder<?> child : children) {

                                sql.append(", ")
                                                .append(
                                                                buildRelationship(
                                                                                child,
                                                                                metadata,
                                                                                metadata.getTableName()));
                        }
                }

                sql.append("\nFROM ")
                                .append(metadata.getTableName());

                String softDeleteCondition = buildSoftDeleteCondition(
                                metadata,
                                metadata.getTableName());

                if (!softDeleteCondition.isBlank()) {
                        sql.append("\nWHERE ")
                                        .append(softDeleteCondition);
                }

                return sql.toString();
        }

        private String buildRootSelect() {

                if (columns == null || columns.isEmpty()) {

                        return metadata.getTableName() + ".*";
                }

                return columns.stream()
                                .map(column -> metadata.getTableName()
                                                + "."
                                                + column)
                                .collect(Collectors.joining(", "));
        }

        public List<?> execute() {

                validateExecution();

                Query query = entityManager.createNativeQuery(
                                build());

                return query.getResultList();
        }

        public <R> List<R> execute(
                        Class<R> resultType) {

                validateExecution();

                Query query = entityManager.createNativeQuery(
                                build());

                List<?> rows = query.getResultList();

                return rows.stream()
                                .map(row -> mapRow(
                                                row,
                                                resultType))
                                .toList();
        }

        private void validateExecution() {

                if (entityManager == null) {
                        throw new IllegalStateException(
                                        "EntityManager is required to execute query");
                }

                if (objectMapper == null) {
                        throw new IllegalStateException(
                                        "ObjectMapper is required to map query result");
                }
        }

        private <R> R mapRow(
                        Object row,
                        Class<R> resultType) {

                if (row == null) {
                        return null;
                }

                if (isSimpleType(resultType)) {

                        return objectMapper.convertValue(
                                        extractSingleValue(row),
                                        resultType);
                }

                Object[] rowValues = row instanceof Object[] values
                                ? values
                                : new Object[] { row };

                Map<String, Object> values = new LinkedHashMap<>();

                List<String> rootColumns = getRootSelectedColumns();

                List<String> relationshipColumns = getRelationshipColumns();

                int index = 0;

                for (String column : rootColumns) {

                        if (index >= rowValues.length) {

                                throw new IllegalStateException(
                                                "Result column mismatch. " +
                                                                "Expected root column: " + column);
                        }

                        values.put(
                                        column,
                                        rowValues[index++]);
                }

                for (String relationship : relationshipColumns) {

                        if (index >= rowValues.length) {

                                throw new IllegalStateException(
                                                "Result relationship mismatch. " +
                                                                "Expected relationship: "
                                                                + relationship);
                        }

                        Object value = rowValues[index++];

                        values.put(
                                        relationship,
                                        parseJson(value));
                }

                return objectMapper.convertValue(
                                values,
                                resultType);
        }

        private Object extractSingleValue(
                        Object row) {

                if (row instanceof Object[] values) {

                        if (values.length != 1) {

                                throw new IllegalStateException(
                                                "Expected single result column but got "
                                                                + values.length);
                        }

                        return values[0];
                }

                return row;
        }

        private List<String> getRootSelectedColumns() {

                if (columns == null || columns.isEmpty()) {

                        return metadata.getColumnNames();
                }

                return columns;
        }

        private List<String> getRelationshipColumns() {

                if (children == null || children.isEmpty()) {

                        return List.of();
                }

                return children.stream()
                                .map(child -> metadata
                                                .getRelationship(
                                                                child.getRelationship())
                                                .getName())
                                .toList();
        }

        private Object parseJson(Object value) {

                if (value == null) {
                        return null;
                }

                if (value instanceof String json) {
                        return readJson(json);
                }

                if (value instanceof CharSequence sequence) {
                        return readJson(sequence.toString());
                }

                return value;
        }

        private Object readJson(String json) {

                if (json == null || json.isBlank()) {
                        return null;
                }

                try {
                        return objectMapper.readValue(
                                        json,
                                        Object.class);
                } catch (Exception e) {
                        throw new IllegalStateException(
                                        "Failed to parse JSON result",
                                        e);
                }
        }

        private boolean isSimpleType(
                        Class<?> type) {

                return type.isPrimitive()
                                || type == String.class
                                || type == UUID.class
                                || type == Boolean.class
                                || type == Character.class
                                || Number.class.isAssignableFrom(type)
                                || type.isEnum();
        }

        private String buildRelationship(
                        ChildBuilder<?> child,
                        EntityMetadata<?> parentMetadata,
                        String parentTable) {

                RelationshipMetadata relationship = parentMetadata.getRelationship(
                                child.getRelationship());

                return switch (relationship.getType()) {

                        case ONE_TO_ONE, MANY_TO_ONE ->
                                buildObjectRelationship(
                                                child,
                                                relationship,
                                                parentMetadata,
                                                parentTable);

                        case ONE_TO_MANY, MANY_TO_MANY ->
                                buildArrayRelationship(
                                                child,
                                                relationship,
                                                parentMetadata,
                                                parentTable);
                };
        }

        // =========================================================
        // ROOT OBJECT RELATIONSHIP
        // =========================================================

        private String buildObjectRelationship(
                        ChildBuilder<?> child,
                        RelationshipMetadata relationship,
                        EntityMetadata<?> parentMetadata,
                        String parentTable) {

                EntityMetadata<?> childMetadata = child.getMetadata();

                String childTable = childMetadata.getTableName();

                String childAlias = createAlias(
                                "child",
                                child.getRelationship());

                String condition = buildObjectCondition(
                                relationship,
                                childMetadata,
                                parentMetadata,
                                childAlias,
                                parentTable);

                condition = appendSoftDeleteCondition(
                                condition,
                                childMetadata,
                                childAlias);

                return """
                                (
                                    SELECT %s
                                    FROM %s %s
                                    WHERE %s
                                ) AS %s
                                """.formatted(
                                buildJsonObject(
                                                child,
                                                childAlias),
                                childTable,
                                childAlias,
                                condition,
                                relationship.getName());
        }

        // =========================================================
        // ROOT ARRAY RELATIONSHIP
        // =========================================================

        private String buildArrayRelationship(
                        ChildBuilder<?> child,
                        RelationshipMetadata relationship,
                        EntityMetadata<?> parentMetadata,
                        String parentTable) {

                EntityMetadata<?> childMetadata = child.getMetadata();

                String childTable = childMetadata.getTableName();

                String childAlias = createAlias(
                                "child",
                                child.getRelationship());

                String fromClause;
                String condition;

                if (relationship.isManyToMany()) {

                        String joinTable = relationship.getJoinTable();

                        String joinAlias = createAlias(
                                        "join",
                                        child.getRelationship());

                        fromClause = """
                                        %s %s
                                        JOIN %s %s
                                            ON %s.%s = %s.%s
                                        """.formatted(
                                        childTable,
                                        childAlias,
                                        joinTable,
                                        joinAlias,
                                        joinAlias,
                                        relationship.getInverseJoinColumn(),
                                        childAlias,
                                        childMetadata.getIdentifier());

                        condition = """
                                        %s.%s = %s.%s
                                        """.formatted(
                                        joinAlias,
                                        relationship.getJoinColumn(),
                                        parentTable,
                                        parentMetadata.getIdentifier());

                } else {

                        fromClause = childTable
                                        + " "
                                        + childAlias;

                        condition = """
                                        %s.%s = %s.%s
                                        """.formatted(
                                        childAlias,
                                        relationship.getJoinColumnName(),
                                        parentTable,
                                        parentMetadata.getIdentifier());
                }

                condition = appendSoftDeleteCondition(
                                condition,
                                childMetadata,
                                childAlias);

                return """
                                (
                                    SELECT COALESCE(
                                        json_agg(%s),
                                        '[]'::json
                                    )
                                    FROM %s
                                    WHERE %s
                                ) AS %s
                                """.formatted(
                                buildJsonObject(
                                                child,
                                                childAlias),
                                fromClause.strip(),
                                condition.strip(),
                                relationship.getName());
        }

        // =========================================================
        // OBJECT RELATIONSHIP CONDITION
        // =========================================================

        private String buildObjectCondition(
                        RelationshipMetadata relationship,
                        EntityMetadata<?> childMetadata,
                        EntityMetadata<?> parentMetadata,
                        String childAlias,
                        String parentTable) {

                if (relationship.isManyToOne()) {

                        return "%s.%s = %s.%s".formatted(
                                        childAlias,
                                        childMetadata.getIdentifier(),
                                        parentTable,
                                        relationship.getJoinColumnName());
                }

                return "%s.%s = %s.%s".formatted(
                                childAlias,
                                relationship.getJoinColumnName(),
                                parentTable,
                                parentMetadata.getIdentifier());
        }

        // =========================================================
        // JSON OBJECT
        // =========================================================

        private String buildJsonObject(
                        ChildBuilder<?> child,
                        String tableAlias) {

                EntityMetadata<?> childMetadata = child.getMetadata();

                List<String> selectedColumns = child.getSelectedColumns();

                if (selectedColumns == null
                                || selectedColumns.isEmpty()) {

                        selectedColumns = childMetadata.getColumnNames();
                }

                String fields = selectedColumns.stream()
                                .map(column -> "'%s', %s.%s".formatted(
                                                column,
                                                tableAlias,
                                                column))
                                .collect(
                                                Collectors.joining(", "));

                StringBuilder json = new StringBuilder(
                                "json_build_object("
                                                + fields);

                if (child.getChildren() != null) {

                        for (ChildBuilder<?> nested : child.getChildren()) {

                                json.append(", '")
                                                .append(
                                                                nested.getRelationship())
                                                .append("', ")
                                                .append(
                                                                buildNestedRelationship(
                                                                                nested,
                                                                                childMetadata,
                                                                                tableAlias));
                        }
                }

                return json
                                .append(")")
                                .toString();
        }

        // =========================================================
        // NESTED RELATIONSHIP
        // =========================================================

        private String buildNestedRelationship(
                        ChildBuilder<?> child,
                        EntityMetadata<?> parentMetadata,
                        String parentAlias) {

                RelationshipMetadata relationship = parentMetadata.getRelationship(
                                child.getRelationship());

                return switch (relationship.getType()) {

                        case ONE_TO_ONE, MANY_TO_ONE ->
                                buildNestedObject(
                                                child,
                                                relationship,
                                                parentMetadata,
                                                parentAlias);

                        case ONE_TO_MANY, MANY_TO_MANY ->
                                buildNestedArray(
                                                child,
                                                relationship,
                                                parentMetadata,
                                                parentAlias);
                };
        }

        // =========================================================
        // NESTED OBJECT
        // =========================================================

        private String buildNestedObject(
                        ChildBuilder<?> child,
                        RelationshipMetadata relationship,
                        EntityMetadata<?> parentMetadata,
                        String parentAlias) {

                EntityMetadata<?> childMetadata = child.getMetadata();

                String childAlias = createAlias(
                                "nested",
                                child.getRelationship());

                String condition = buildObjectCondition(
                                relationship,
                                childMetadata,
                                parentMetadata,
                                childAlias,
                                parentAlias);

                condition = appendSoftDeleteCondition(
                                condition,
                                childMetadata,
                                childAlias);

                return """
                                (
                                    SELECT %s
                                    FROM %s %s
                                    WHERE %s
                                )
                                """.formatted(
                                buildJsonObject(
                                                child,
                                                childAlias),
                                childMetadata.getTableName(),
                                childAlias,
                                condition);
        }

        // =========================================================
        // NESTED ARRAY
        // =========================================================

        private String buildNestedArray(
                        ChildBuilder<?> child,
                        RelationshipMetadata relationship,
                        EntityMetadata<?> parentMetadata,
                        String parentAlias) {

                EntityMetadata<?> childMetadata = child.getMetadata();

                String childAlias = createAlias(
                                "nested",
                                child.getRelationship());

                String fromClause;
                String condition;

                if (relationship.isManyToMany()) {

                        String joinAlias = createAlias(
                                "nested_join",
                                child.getRelationship());

                        fromClause = """
                                        %s %s
                                        JOIN %s %s
                                            ON %s.%s = %s.%s
                                        """.formatted(
                                        childMetadata.getTableName(),
                                        childAlias,
                                        relationship.getJoinTable(),
                                        joinAlias,
                                        joinAlias,
                                        relationship.getInverseJoinColumn(),
                                        childAlias,
                                        childMetadata.getIdentifier());

                        condition = """
                                        %s.%s = %s.%s
                                        """.formatted(
                                        joinAlias,
                                        relationship.getJoinColumn(),
                                        parentAlias,
                                        parentMetadata.getIdentifier());

                } else {

                        fromClause = childMetadata.getTableName()
                                        + " "
                                        + childAlias;

                        condition = """
                                        %s.%s = %s.%s
                                        """.formatted(
                                        childAlias,
                                        relationship.getJoinColumnName(),
                                        parentAlias,
                                        parentMetadata.getIdentifier());
                }

                condition = appendSoftDeleteCondition(
                                condition,
                                childMetadata,
                                childAlias);

                return """
                                (
                                    SELECT COALESCE(
                                        json_agg(%s),
                                        '[]'::json
                                    )
                                    FROM %s
                                    WHERE %s
                                )
                                """.formatted(
                                buildJsonObject(
                                                child,
                                                childAlias),
                                fromClause.strip(),
                                condition.strip());
        }

        // =========================================================
        // SOFT DELETE
        // =========================================================

        private String buildSoftDeleteCondition(
                        EntityMetadata<?> metadata,
                        String alias) {

                if (!metadata.hasSoftDelete()) {
                        return "";
                }

                return "%s.%s IS NULL".formatted(
                                alias,
                                metadata.getSoftDeleteColumn());
        }

        private String appendSoftDeleteCondition(
                        String condition,
                        EntityMetadata<?> metadata,
                        String alias) {

                String softDeleteCondition = buildSoftDeleteCondition(
                                metadata,
                                alias);

                if (softDeleteCondition.isBlank()) {
                        return condition;
                }

                return condition
                                + "\nAND "
                                + softDeleteCondition;
        }

        // =========================================================
        // ALIAS
        // =========================================================

        private String createAlias(
                        String prefix,
                        String relationship) {

                return prefix
                                + "_"
                                + sanitizeAlias(relationship);
        }

        private String sanitizeAlias(
                        String value) {

                return value
                                .replaceAll(
                                                "[^a-zA-Z0-9_]",
                                                "_");
        }

        // =========================================================
        // GETTERS
        // =========================================================

        public EntityMetadata<T> getMetadata() {
                return metadata;
        }

        public List<String> getSelectedColumns() {
                return columns;
        }

        public List<ChildBuilder<?>> getChildren() {
                return children;
        }

        // =========================================================
        // CHILD BUILDER
        // =========================================================

        public static <T> ChildBuilder<T> child(
                        Class<T> entityClass,
                        String relationship) {

                return new ChildBuilder<>(
                                entityClass,
                                relationship);
        }

        public static class ChildBuilder<T>
                        extends QueryBuilder<T> {

                private final String relationship;

                public ChildBuilder(
                                Class<T> entityClass,
                                String relationship) {

                        super(entityClass);

                        this.relationship = relationship;
                }

                @Override
                public ChildBuilder<T> select(
                                String... columns) {

                        super.select(columns);

                        return this;
                }

                @Override
                public ChildBuilder<T> with(
                                List<ChildBuilder<?>> children) {

                        super.with(children);

                        return this;
                }

                public String getRelationship() {
                        return relationship;
                }
        }
}