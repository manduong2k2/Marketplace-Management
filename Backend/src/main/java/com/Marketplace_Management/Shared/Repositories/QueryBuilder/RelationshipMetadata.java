package com.Marketplace_Management.Shared.Repositories.QueryBuilder;

import jakarta.persistence.*;

import java.lang.reflect.Field;

public class RelationshipMetadata {

    private final Field field;
    private final Class<?> targetEntity;
    private final RelationshipType type;

    private final String mappedBy;
    private final String joinTable;
    private final String joinColumn;
    private final String inverseJoinColumn;
    private final String joinColumnName;

    public RelationshipMetadata(Field field) {
        this.field = field;
        this.targetEntity = EntityMetadata.resolveTargetEntity(field);
        this.type = resolveType(field);

        this.mappedBy = resolveMappedBy(field);
        this.joinTable = resolveJoinTable(field);
        this.joinColumn = resolveJoinColumn(field);
        this.inverseJoinColumn = resolveInverseJoinColumn(field);
        this.joinColumnName = resolveJoinColumnName(field);
    }

    private RelationshipType resolveType(Field field) {

        if (field.isAnnotationPresent(OneToOne.class)) {
            return RelationshipType.ONE_TO_ONE;
        }

        if (field.isAnnotationPresent(OneToMany.class)) {
            return RelationshipType.ONE_TO_MANY;
        }

        if (field.isAnnotationPresent(ManyToOne.class)) {
            return RelationshipType.MANY_TO_ONE;
        }

        if (field.isAnnotationPresent(ManyToMany.class)) {
            return RelationshipType.MANY_TO_MANY;
        }

        throw new IllegalArgumentException(
                "Field is not a relationship: "
                        + field.getName()
        );
    }

    private String resolveMappedBy(Field field) {

        if (field.isAnnotationPresent(OneToOne.class)) {
            return field.getAnnotation(OneToOne.class).mappedBy();
        }

        if (field.isAnnotationPresent(OneToMany.class)) {
            return field.getAnnotation(OneToMany.class).mappedBy();
        }

        if (field.isAnnotationPresent(ManyToMany.class)) {
            return field.getAnnotation(ManyToMany.class).mappedBy();
        }

        return "";
    }

    private JoinTable getJoinTableAnnotation() {
        return field.getAnnotation(JoinTable.class);
    }

    private String resolveJoinTable(Field field) {

        JoinTable table = getJoinTableAnnotation();

        return table != null && !table.name().isBlank()
                ? table.name()
                : null;
    }

    private String resolveJoinColumn(Field field) {

        JoinTable table = getJoinTableAnnotation();

        if (table == null || table.joinColumns().length == 0) {
            return null;
        }

        return table.joinColumns()[0].name();
    }

    private String resolveInverseJoinColumn(Field field) {

        JoinTable table = getJoinTableAnnotation();

        if (table == null || table.inverseJoinColumns().length == 0) {
            return null;
        }

        return table.inverseJoinColumns()[0].name();
    }

    private String resolveJoinColumnName(Field field) {

        JoinColumn joinColumn =
                field.getAnnotation(JoinColumn.class);

        if (joinColumn != null && !joinColumn.name().isBlank()) {
            return joinColumn.name();
        }

        if (mappedBy.isBlank()) {
            return null;
        }

        return resolveMappedByJoinColumn();
    }

    private String resolveMappedByJoinColumn() {

        Field mappedField = findField(
                targetEntity,
                mappedBy
        );

        if (mappedField == null) {
            return null;
        }

        JoinColumn joinColumn =
                mappedField.getAnnotation(JoinColumn.class);

        return joinColumn != null
                ? joinColumn.name()
                : null;
    }

    private Field findField(
            Class<?> type,
            String fieldName) {

        Class<?> current = type;

        while (current != null && current != Object.class) {

            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }

        return null;
    }

    public boolean isManyToMany() {
        return type == RelationshipType.MANY_TO_MANY;
    }

    public boolean isManyToOne() {
        return type == RelationshipType.MANY_TO_ONE;
    }

    public boolean isOneToMany() {
        return type == RelationshipType.ONE_TO_MANY;
    }

    public boolean isOneToOne() {
        return type == RelationshipType.ONE_TO_ONE;
    }

    public Field getField() {
        return field;
    }

    public String getName() {
        return field.getName();
    }

    public Class<?> getTargetEntity() {
        return targetEntity;
    }

    public RelationshipType getType() {
        return type;
    }

    public String getMappedBy() {
        return mappedBy;
    }

    public String getJoinTable() {
        return joinTable;
    }

    public String getJoinColumn() {
        return joinColumn;
    }

    public String getInverseJoinColumn() {
        return inverseJoinColumn;
    }

    public String getJoinColumnName() {
        return joinColumnName;
    }
}