package com.Marketplace_Management.Shared.Repositories.QueryBuilder;

import jakarta.persistence.*;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hibernate.annotations.SoftDelete;

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

    private String resolveTableName(Class<?> type) {
        Table table = type.getAnnotation(Table.class);

        return table != null && !table.name().isBlank()
                ? table.name()
                : type.getSimpleName();
    }

    private List<ColumnMetadata> resolveColumns(Class<?> type) {
        List<ColumnMetadata> result = new ArrayList<>();

        for (Field field : getAllFields(type)) {
            if (isColumn(field)) {
                result.add(new ColumnMetadata(
                        resolveColumnName(field),
                        field.getType()));
            }
        }

        return result;
    }

    private boolean isColumn(Field field) {
        return field.isAnnotationPresent(Id.class)
                || field.isAnnotationPresent(Column.class)
                || field.isAnnotationPresent(JoinColumn.class);
    }

    private String resolveColumnName(Field field) {

        Column column = field.getAnnotation(Column.class);

        if (column != null && !column.name().isBlank()) {
            return column.name();
        }

        JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);

        if (joinColumn != null && !joinColumn.name().isBlank()) {
            return joinColumn.name();
        }

        return field.getName();
    }

    private List<RelationshipMetadata> resolveRelationships(Class<?> type) {
        return getAllFields(type).stream()
                .filter(this::isRelationship)
                .map(RelationshipMetadata::new)
                .toList();
    }

    private boolean isRelationship(Field field) {
        return field.isAnnotationPresent(OneToOne.class)
                || field.isAnnotationPresent(OneToMany.class)
                || field.isAnnotationPresent(ManyToOne.class)
                || field.isAnnotationPresent(ManyToMany.class);
    }

    private List<Field> getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();

        Class<?> current = type;

        while (current != null && current != Object.class) {
            fields.addAll(Arrays.asList(current.getDeclaredFields()));
            current = current.getSuperclass();
        }

        return fields;
    }

    public RelationshipMetadata getRelationship(String name) {
        return relationships.stream()
                .filter(relationship -> relationship.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Relationship " + name
                                + " is not allowed for "
                                + entityClass.getSimpleName()));
    }

    public void validateColumns(String... selectedColumns) {
        for (String column : selectedColumns) {
            if (!getColumnNames().contains(column)) {
                throw new IllegalArgumentException(
                        "Column " + column + " is not allowed for "
                                + entityClass.getSimpleName() + ". Allowed columns: " + getColumnNames());
            }
        }
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public String getTableName() {
        return tableName;
    }

    public String getIdentifier() {
        return getAllFields(entityClass).stream()
                .filter(field -> field.isAnnotationPresent(Id.class))
                .map(this::resolveColumnName)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No @Id found for "
                                + entityClass.getSimpleName()));
    }

    public List<ColumnMetadata> getColumns() {
        return columns;
    }

    public List<String> getColumnNames() {
        return columns.stream()
                .map(ColumnMetadata::getName)
                .toList();
    }

    public List<RelationshipMetadata> getRelationships() {
        return relationships;
    }

    public static Class<?> resolveTargetEntity(Field field) {

        Class<?> fieldType = field.getType();

        if (!java.util.Collection.class.isAssignableFrom(fieldType)) {
            return fieldType;
        }

        Type genericType = field.getGenericType();

        if (genericType instanceof ParameterizedType parameterizedType) {

            Type[] arguments = parameterizedType.getActualTypeArguments();

            if (arguments.length == 1
                    && arguments[0] instanceof Class<?> clazz) {
                return clazz;
            }
        }

        throw new IllegalArgumentException(
                "Cannot resolve target entity for field: "
                        + field.getName());
    }

    public boolean hasSoftDelete() {
        return getSoftDelete() != null;
    }

    public String getSoftDeleteColumn() {

        SoftDelete softDelete = getSoftDelete();

        if (softDelete == null) {
            return null;
        }

        String columnName = softDelete.columnName();

        return columnName.isBlank()
                ? "deleted"
                : columnName;
    }

    private SoftDelete getSoftDelete() {

        Class<?> current = entityClass;

        while (current != null
                && current != Object.class) {

            SoftDelete annotation = current.getAnnotation(SoftDelete.class);

            if (annotation != null) {
                return annotation;
            }

            current = current.getSuperclass();
        }

        return null;
    }
}