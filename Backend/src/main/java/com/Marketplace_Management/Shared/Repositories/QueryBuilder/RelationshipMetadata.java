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

    public RelationshipMetadata(
            Field field,
            Class<?> targetEntity,
            RelationshipType type
    ) {
        this.field = field;
        this.targetEntity = targetEntity;
        this.type = type;

        this.mappedBy = resolveMappedBy(field);

        this.joinTable = resolveJoinTable(field);
        this.joinColumn = resolveJoinColumn(field);
        this.inverseJoinColumn = resolveInverseJoinColumn(field);

        this.joinColumnName = resolveJoinColumnName(field);
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

    private String resolveJoinTable(Field field) {
        JoinTable annotation = field.getAnnotation(JoinTable.class);

        return annotation != null
                ? annotation.name()
                : null;
    }

    private String resolveJoinColumn(Field field) {
        JoinTable annotation = field.getAnnotation(JoinTable.class);

        if (annotation == null || annotation.joinColumns().length == 0) {
            return null;
        }

        return annotation.joinColumns()[0].name();
    }

    private String resolveInverseJoinColumn(Field field) {
        JoinTable annotation = field.getAnnotation(JoinTable.class);

        if (annotation == null || annotation.inverseJoinColumns().length == 0) {
            return null;
        }

        return annotation.inverseJoinColumns()[0].name();
    }

    private String resolveJoinColumnName(Field field) {
        JoinColumn annotation = field.getAnnotation(JoinColumn.class);

        if (annotation != null) {
            return annotation.name();
        }

        // Handle mappedBy relationships - look at the target entity
        String mappedBy = resolveMappedBy(field);
        if (!mappedBy.isEmpty()) {
            try {
                Field mappedByField = targetEntity.getDeclaredField(mappedBy);
                JoinColumn mappedByAnnotation = mappedByField.getAnnotation(JoinColumn.class);
                if (mappedByAnnotation != null) {
                    return mappedByAnnotation.name();
                }
            } catch (NoSuchFieldException e) {
                // Field not found, return null
            }
        }

        return null;
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