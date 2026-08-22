package com.Marketplace_Management.Shared.Repositories.QueryBuilder;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntityMetadata<T> {

    private final Class<?> entityClass;
    private final String tableName;
    private final List<ColumnMetadata> columns;
    private final List<RelationshipMetadata> relationships;

    public EntityMetadata(Class<?> entityClass) {
        this.entityClass = entityClass;
        this.tableName = resolveTableName(entityClass);
        this.columns = resolveColumns(entityClass);
        this.relationships = resolveRelationships(entityClass);
    }

    private String resolveTableName(Class<?> entityClass) {
        Table table = entityClass.getAnnotation(Table.class);

        if (table != null && !table.name().isBlank()) {
            return table.name();
        }

        return entityClass.getSimpleName();
    }

    private List<ColumnMetadata> resolveColumns(Class<?> entityClass) {
        List<ColumnMetadata> columns = new ArrayList<>();

        Class<?> currentClass = entityClass;
        while (currentClass != null && currentClass != Object.class) {

            Arrays.stream(currentClass.getDeclaredFields())
                    .filter(this::isColumn)
                    .forEach(field -> columns.add(
                            new ColumnMetadata(
                                    resolveColumnName(field),
                                    field.getType())));

            currentClass = currentClass.getSuperclass();
        }

        return columns;
    }

    private boolean isColumn(Field field) {
        return field.isAnnotationPresent(Column.class)
                || field.getAnnotation(jakarta.persistence.Id.class) != null
                || field.isAnnotationPresent(JoinColumn.class);
    }

    private String resolveColumnName(Field field) {
        Column column = field.getAnnotation(Column.class);
        JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);

        if (column != null && !column.name().isBlank()) {
            return column.name();
        }

        if (joinColumn != null && !joinColumn.name().isBlank()) {
            return joinColumn.name();
        }

        return field.getName();
    }

    private List<RelationshipMetadata> resolveRelationships(Class<?> entityClass) {
        List<RelationshipMetadata> relationships = new ArrayList<>();

        Class<?> currentClass = entityClass;

        while (currentClass != null && currentClass != Object.class) {

            for (Field field : currentClass.getDeclaredFields()) {

                if (field.isAnnotationPresent(OneToOne.class)) {
                    relationships.add(
                            new RelationshipMetadata(
                                    field,
                                    resolveTargetEntity(field),
                                    RelationshipType.ONE_TO_ONE));
                }

                if (field.isAnnotationPresent(OneToMany.class)) {
                    relationships.add(
                            new RelationshipMetadata(
                                    field,
                                    resolveTargetEntity(field),
                                    RelationshipType.ONE_TO_MANY));
                }

                if (field.isAnnotationPresent(ManyToOne.class)) {
                    relationships.add(
                            new RelationshipMetadata(
                                    field,
                                    resolveTargetEntity(field),
                                    RelationshipType.MANY_TO_ONE));
                }

                if (field.isAnnotationPresent(ManyToMany.class)) {
                    relationships.add(
                            new RelationshipMetadata(
                                    field,
                                    resolveTargetEntity(field),
                                    RelationshipType.MANY_TO_MANY));
                }
            }

            currentClass = currentClass.getSuperclass();
        }

        return relationships;
    }

    private Class<?> resolveTargetEntity(Field field) {
        Class<?> type = field.getType();

        if (!java.util.Collection.class.isAssignableFrom(type)) {
            return type;
        }

        java.lang.reflect.Type genericType = field.getGenericType();

        if (genericType instanceof java.lang.reflect.ParameterizedType parameterizedType) {
            java.lang.reflect.Type[] arguments = parameterizedType.getActualTypeArguments();

            if (arguments.length == 1
                    && arguments[0] instanceof Class<?> clazz) {
                return clazz;
            }
        }

        throw new IllegalArgumentException(
                "Cannot resolve target entity for field: " + field.getName());
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public String getTableName() {
        return tableName;
    }

    public String getIdentifier() {
        Class<?> currentClass = entityClass;

        while (currentClass != null && currentClass != Object.class) {
            for (Field field : currentClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(jakarta.persistence.Id.class)) {
                    return resolveColumnName(field);
                }
            }

            currentClass = currentClass.getSuperclass();
        }

        return null;
    }

    public List<ColumnMetadata> getColumns() {
        return columns;
    }

    public List<String> getColumnNames() {
        return columns.stream().map(ColumnMetadata::getName).toList();
    }

    public List<RelationshipMetadata> getRelationships() {
        return relationships;
    }
}
