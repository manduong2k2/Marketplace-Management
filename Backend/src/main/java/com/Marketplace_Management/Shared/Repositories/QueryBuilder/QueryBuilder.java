package com.Marketplace_Management.Shared.Repositories.QueryBuilder;

import java.util.List;
import java.util.stream.Collectors;

public class QueryBuilder<T> {

    private final EntityMetadata<T> metadata;

    private List<String> columns;
    private List<ChildBuilder<?>> children;

    public QueryBuilder(Class<T> entityClass) {
        this.metadata = new EntityMetadata<>(entityClass);
    }

    public QueryBuilder<T> query() {
        return this;
    }

    public QueryBuilder<T> select(String... columns) {
        var allowedColumns = metadata.getColumnNames();

        for (String column : columns) {
            if (!allowedColumns.contains(column)) {
                throw new IllegalArgumentException(
                        "Column " + column + " is not allowed");
            }
        }

        this.columns = List.of(columns);

        return this;
    }

    public QueryBuilder<T> with(List<ChildBuilder<?>> children) {
        this.children = children;

        return this;
    }

    public String build() {
        StringBuilder query = new StringBuilder();

        query.append("SELECT ");

        if (columns == null || columns.isEmpty()) {
            query.append(metadata.getTableName()).append(".*");
        } else {
            query.append(
                    columns.stream()
                            .map(column -> metadata.getTableName() + "." + column)
                            .collect(Collectors.joining(", ")));
        }

        if (children != null) {
            for (ChildBuilder<?> child : children) {
                query.append(", ");
                query.append(buildChild(child));
            }
        }

        query.append(" FROM ");
        query.append(metadata.getTableName());

        return query.toString();
    }

    private String buildChild(ChildBuilder<?> child) {

        RelationshipMetadata relationship = metadata.getRelationships()
                .stream()
                .filter(r -> r.getName().equals(child.getRelationship()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Relationship "
                                + child.getRelationship()
                                + " is not allowed for "
                                + metadata.getEntityClass().getSimpleName()));

        return switch (relationship.getType()) {

            case ONE_TO_ONE, MANY_TO_ONE ->
                buildObjectRelationship(child, relationship);

            case ONE_TO_MANY, MANY_TO_MANY ->
                buildArrayRelationship(child, relationship);
        };
    }

    private String buildObjectRelationship(
            ChildBuilder<?> child,
            RelationshipMetadata relationship) {
        EntityMetadata<?> childMetadata = child.getMetadata();

        String childTable = childMetadata.getTableName();

        String jsonObject = buildJsonObject(child);

        String condition;

        if (relationship.getType() == RelationshipType.MANY_TO_ONE) {

            condition = childTable + "."
                    + childMetadata.getIdentifier()
                    + " = "
                    + metadata.getTableName() + "."
                    + relationship.getJoinColumnName();

        } else {

            condition = childTable + "."
                    + relationship.getJoinColumnName()
                    + " = "
                    + metadata.getTableName() + "."
                    + metadata.getIdentifier();
        }

        return """
                (
                    SELECT %s
                    FROM %s
                    WHERE %s
                ) AS %s
                """.formatted(
                jsonObject,
                childTable,
                condition,
                relationship.getName());
    }

    private String buildArrayRelationship(
            ChildBuilder<?> child,
            RelationshipMetadata relationship) {
        EntityMetadata<?> childMetadata = child.getMetadata();

        String childTable = childMetadata.getTableName();
        String parentTable = metadata.getTableName();

        String jsonObject = buildJsonObject(child);

        String fromClause;
        String condition;

        if (relationship.getType() == RelationshipType.MANY_TO_MANY) {

            fromClause = """
                    %s
                    JOIN %s
                        ON %s.%s = %s.%s
                    """.formatted(
                    childTable,
                    relationship.getJoinTable(),

                    relationship.getJoinTable(),
                    relationship.getInverseJoinColumn(),

                    childTable,
                    childMetadata.getIdentifier());

            condition = """
                    %s.%s = %s.%s
                    """.formatted(
                    relationship.getJoinTable(),
                    relationship.getJoinColumn(),

                    parentTable,
                    metadata.getIdentifier());

        } else {

            fromClause = childTable;

            condition = """
                    %s.%s = %s.%s
                    """.formatted(
                    childTable,
                    relationship.getJoinColumnName(),

                    parentTable,
                    metadata.getIdentifier());
        }

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
                jsonObject,
                fromClause.strip(),
                condition.strip(),
                relationship.getName());
    }

    private String buildJsonObject(ChildBuilder<?> child) {

        EntityMetadata<?> childMetadata = child.getMetadata();

        // Quan trọng: lấy columns mà child SELECT,
        // không phải toàn bộ metadata columns.
        List<String> columns = child.getSelectedColumns();

        if (columns == null || columns.isEmpty()) {
            columns = childMetadata.getColumnNames();
        }

        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("json_build_object(");

        // Add regular columns
        String columnPart = columns.stream()
                .map(column -> "'" + column + "', " +
                        childMetadata.getTableName() +
                        "." + column)
                .collect(Collectors.joining(", "));

        jsonBuilder.append(columnPart);

        // Add nested children relationships
        List<ChildBuilder<?>> childChildren = child.getChildren();
        if (childChildren != null && !childChildren.isEmpty()) {
            for (ChildBuilder<?> nestedChild : childChildren) {
                // Build nested child as a subquery with key
                String nestedJson = buildNestedChild(nestedChild, childMetadata);
                jsonBuilder.append(", '").append(nestedChild.getRelationship()).append("', ").append(nestedJson);
            }
        }

        jsonBuilder.append(")");

        return jsonBuilder.toString();
    }

    private String buildNestedChild(ChildBuilder<?> nestedChild, EntityMetadata<?> parentMetadata) {
        // Get the relationship metadata for the nested child
        RelationshipMetadata relationship = parentMetadata.getRelationships()
                .stream()
                .filter(r -> r.getName().equals(nestedChild.getRelationship()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Relationship "
                                + nestedChild.getRelationship()
                                + " is not allowed for "
                                + parentMetadata.getEntityClass().getSimpleName()));

        // Build the nested relationship
        return buildChildWithParent(nestedChild, relationship, parentMetadata);
    }

    private String buildChildWithParent(ChildBuilder<?> child, RelationshipMetadata relationship, EntityMetadata<?> parentMetadata) {
        return switch (relationship.getType()) {
            case ONE_TO_ONE, MANY_TO_ONE -> buildObjectRelationshipWithParent(child, relationship, parentMetadata);
            case ONE_TO_MANY, MANY_TO_MANY -> buildArrayRelationshipWithParent(child, relationship, parentMetadata);
        };
    }

    private String buildObjectRelationshipWithParent(ChildBuilder<?> child, RelationshipMetadata relationship, EntityMetadata<?> parentMetadata) {
        EntityMetadata<?> childMetadata = child.getMetadata();
        String childTable = childMetadata.getTableName();
        String parentTable = parentMetadata.getTableName();

        String jsonObject = buildJsonObject(child);

        String condition;

        if (relationship.getType() == RelationshipType.MANY_TO_ONE) {
            condition = childTable + "."
                    + childMetadata.getIdentifier()
                    + " = "
                    + parentTable + "."
                    + relationship.getJoinColumnName();
        } else {
            condition = childTable + "."
                    + relationship.getJoinColumnName()
                    + " = "
                    + parentTable + "."
                    + parentMetadata.getIdentifier();
        }

        return """
                (
                    SELECT %s
                    FROM %s
                    WHERE %s
                )
                """.formatted(
                jsonObject,
                childTable,
                condition);
    }

    private String buildArrayRelationshipWithParent(ChildBuilder<?> child, RelationshipMetadata relationship, EntityMetadata<?> parentMetadata) {
        EntityMetadata<?> childMetadata = child.getMetadata();
        String childTable = childMetadata.getTableName();
        String parentTable = parentMetadata.getTableName();

        String jsonObject = buildJsonObject(child);

        String fromClause;
        String condition;

        if (relationship.getType() == RelationshipType.MANY_TO_MANY) {
            fromClause = """
                    %s
                    JOIN %s
                        ON %s.%s = %s.%s
                    """.formatted(
                    childTable,
                    relationship.getJoinTable(),
                    relationship.getJoinTable(),
                    relationship.getInverseJoinColumn(),
                    childTable,
                    childMetadata.getIdentifier());

            condition = """
                    %s.%s = %s.%s
                    """.formatted(
                    relationship.getJoinTable(),
                    relationship.getJoinColumn(),
                    parentTable,
                    parentMetadata.getIdentifier());
        } else {
            fromClause = childTable;
            condition = """
                    %s.%s = %s.%s
                    """.formatted(
                    childTable,
                    relationship.getJoinColumnName(),
                    parentTable,
                    parentMetadata.getIdentifier());
        }

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
                jsonObject,
                fromClause.strip(),
                condition.strip());
    }

    public EntityMetadata<T> getMetadata() {
        return metadata;
    }

    public List<String> getSelectedColumns() {
        return columns;
    }

    public List<ChildBuilder<?>> getChildren() {
        return children;
    }

    public static <T> ChildBuilder<T> child(Class<T> entityClass, String relationship) {
        return new ChildBuilder<>(entityClass, relationship);
    }

    public static class ChildBuilder<T> extends QueryBuilder<T> {

        private final String relationship;

        public ChildBuilder(
                Class<T> entityClass,
                String relationship) {
            super(entityClass);
            this.relationship = relationship;
        }

        @Override
        public ChildBuilder<T> select(String... columns) {
            super.select(columns);
            return this;
        }

        @Override
        public ChildBuilder<T> with(List<ChildBuilder<?>> children) {
            super.with(children);
            return this;
        }

        public String getRelationship() {
            return relationship;
        }

        @Override
        public List<ChildBuilder<?>> getChildren() {
            return super.getChildren();
        }
    }
}