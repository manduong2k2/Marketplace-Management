package com.Marketplace_Management.Shared.Utils.QueryBuilder;

import java.util.Map;

public class EntityMetadata {

    private final Class<?> entityClass;
    private final String tableName;
    private final Map<String, ColumnMetadata> columns;
    private final Map<String, RelationshipMetadata> relationships;
    private final boolean hasSoftDelete;

    public EntityMetadata(
            Class<?> entityClass,
            String tableName,
            Map<String, ColumnMetadata> columns,
            Map<String, RelationshipMetadata> relationships,
            boolean hasSoftDelete
    ) {
        this.entityClass = entityClass;
        this.tableName = tableName;
        this.columns = columns;
        this.relationships = relationships;
        this.hasSoftDelete = hasSoftDelete;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public String getTableName() {
        return tableName;
    }

    public Map<String, ColumnMetadata> getColumns() {
        return columns;
    }
    
    public Map<String, RelationshipMetadata> getRelationships() {
        return relationships;
    }
    
    public boolean hasSoftDelete() {
        return hasSoftDelete;
    }
    
    @Override
    public String toString() {
        return "EntityMetadata{" + "\n" +
                "entityClass=" + entityClass + ",\n" +
                "tableName='" + tableName + '\'' + ",\n" +
                "columns=" + columns + ",\n" +
                "relationships=" + relationships + ",\n" +
                "hasSoftDelete=" + hasSoftDelete + "\n" +
                '}';
    }
}
