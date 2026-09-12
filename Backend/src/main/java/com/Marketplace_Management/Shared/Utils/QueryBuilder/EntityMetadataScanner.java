package com.Marketplace_Management.Shared.Utils.QueryBuilder;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.SoftDeleteType;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import com.Marketplace_Management.Marketplace_ManagementApplication;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Component
public class EntityMetadataScanner {

    private final EntityMetadataRegistry registry;

    public EntityMetadataScanner(EntityMetadataRegistry registry) {
        this.registry = registry;
    }

    @PostConstruct
    public void scan() {
        var scanner = new ClassPathScanningCandidateComponentProvider(false);

        scanner.addIncludeFilter(
                new AnnotationTypeFilter(Entity.class));

        String basePackage = Marketplace_ManagementApplication.class.getPackageName();

        var candidates = scanner.findCandidateComponents(basePackage);

        for (var candidate : candidates) {

            try {
                Class<?> entityClass = Class.forName(candidate.getBeanClassName());

                String tableName = resolveTableName(entityClass);

                Map<String, ColumnMetadata> columns = scanColumns(entityClass);

                SoftDelete softDelete = resolveSoftDelete(entityClass);

                Class<?> softDeleteType = softDelete != null
                        ? (softDelete.strategy() == SoftDeleteType.TIMESTAMP ? LocalDateTime.class : Boolean.class)
                        : null;

                if (softDelete != null) {
                    columns.put(
                            softDelete.columnName(),
                            new ColumnMetadata(
                                    Pattern.compile("_(.)").matcher(softDelete.columnName())
                                            .replaceAll(match -> match.group(1).toUpperCase()),
                                    softDelete.columnName(),
                                    softDeleteType));
                }

                Map<String, RelationshipMetadata> relationships = scanRelationships(entityClass);

                EntityMetadata metadata = new EntityMetadata(entityClass, tableName, columns, relationships, softDelete != null);

                registry.register(metadata);

            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String resolveTableName(Class<?> entityClass) {
        Table table = entityClass.getAnnotation(Table.class);

        if (table != null && !table.name().isBlank()) {
            return table.name();
        }

        return entityClass.getSimpleName();
    }

    private Map<String, ColumnMetadata> scanColumns(Class<?> entityClass) {
        Map<String, ColumnMetadata> columns = new HashMap<>();

        Class<?> currentClass = entityClass;

        while (currentClass != null && currentClass != Object.class) {

            for (Field field : currentClass.getDeclaredFields()) {

                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }

                if (isRelationship(field)) {
                    continue;
                }

                String columnName = resolveColumnName(field);

                columns.put(
                        field.getName(),
                        new ColumnMetadata(
                                field.getName(),
                                columnName,
                                field.getType()));
            }

            currentClass = currentClass.getSuperclass();
        }

        return columns;
    }

    private Map<String, RelationshipMetadata> scanRelationships(Class<?> entityClass) {
        Map<String, RelationshipMetadata> relationships = new HashMap<>();

        for (Field field : entityClass.getDeclaredFields()) {

            RelationshipType type = resolveRelationshipType(field);

            if (type == null) {
                continue;
            }

            Class<?> targetEntity = resolveTargetEntity(field);

            String mappedBy = resolveMappedBy(field);

            String joinColumn;

            if (type == RelationshipType.ONE_TO_MANY) {
                joinColumn = resolveOneToManyJoinColumn(field, targetEntity);
            } else {
                joinColumn = resolveJoinColumn(field);
            }

            String joinTable = resolveJoinTable(field);

            String joinColumnTable = resolveInverseJoinColumn(field);

            relationships.put(field.getName(),
                    new RelationshipMetadata(field.getName(), targetEntity, type, joinTable, joinColumn,
                            joinColumnTable, mappedBy));
        }

        return relationships;
    }

    private RelationshipType resolveRelationshipType(Field field) {

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

        return null;
    }

    private String resolveJoinTable(Field field) {
        JoinTable joinTable = field.getAnnotation(JoinTable.class);

        if (joinTable != null && !joinTable.name().isBlank()) {
            return joinTable.name();
        }

        return null;
    }

    private String resolveJoinColumn(Field field) {
        JoinTable joinTable = field.getAnnotation(JoinTable.class);

        if (joinTable != null && joinTable.joinColumns().length > 0) {
            return joinTable.joinColumns()[0].name();
        }

        JoinColumn column = field.getAnnotation(JoinColumn.class);

        return column != null ? column.name() : null;
    }

    private String resolveInverseJoinColumn(Field field) {
        JoinTable joinTable = field.getAnnotation(JoinTable.class);

        if (joinTable != null && joinTable.inverseJoinColumns().length > 0) {
            return joinTable.inverseJoinColumns()[0].name();
        }

        return null;
    }

    private String resolveMappedBy(Field field) {

        OneToMany oneToMany = field.getAnnotation(OneToMany.class);

        if (oneToMany != null && !oneToMany.mappedBy().isBlank()) {
            return oneToMany.mappedBy();
        }

        return null;
    }

    private String resolveOneToManyJoinColumn(
            Field field,
            Class<?> targetEntity) {

        JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);

        if (joinColumn != null && !joinColumn.name().isBlank()) {
            return joinColumn.name();
        }

        String mappedBy = resolveMappedBy(field);

        if (mappedBy == null) {
            return null;
        }

        try {
            Field targetField = targetEntity.getDeclaredField(mappedBy);

            JoinColumn targetJoinColumn = targetField.getAnnotation(JoinColumn.class);

            if (targetJoinColumn != null
                    && !targetJoinColumn.name().isBlank()) {
                return targetJoinColumn.name();
            }

            return camelToSnake(targetField.getName());

        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(
                    "mappedBy field not found: "
                            + targetEntity.getName()
                            + "." + mappedBy,
                    e);
        }
    }

    private Class<?> resolveTargetEntity(Field field) {

        RelationshipType type = resolveRelationshipType(field);

        if (type == RelationshipType.MANY_TO_ONE
                || type == RelationshipType.ONE_TO_ONE) {
            return field.getType();
        }

        Type genericType = field.getGenericType();

        if (!(genericType instanceof ParameterizedType parameterizedType)) {
            throw new IllegalArgumentException(
                    "Collection relationship must be parameterized: "
                            + field);
        }

        Type targetType = parameterizedType.getActualTypeArguments()[0];

        if (!(targetType instanceof Class<?> targetClass)) {
            throw new IllegalArgumentException(
                    "Cannot resolve target entity: " + field);
        }

        return targetClass;
    }

    private String resolveColumnName(Field field) {
        jakarta.persistence.Column column = field.getAnnotation(jakarta.persistence.Column.class);

        if (column != null && !column.name().isBlank()) {
            return column.name();
        }

        return camelToSnake(field.getName());
    }

    private SoftDelete resolveSoftDelete(Class<?> entityClass) {
        Class<?> current = entityClass;

        while (current != null && current != Object.class) {
            SoftDelete annotation = current.getAnnotation(SoftDelete.class);

            if (annotation != null) {
                return annotation;
            }

            current = current.getSuperclass();
        }

        return null;
    }

    private String camelToSnake(String value) {
        return value.replaceAll("([a-z])([A-Z])", "$1_$2")
                .toLowerCase();
    }

    private boolean isRelationship(Field field) {
        return field.isAnnotationPresent(OneToOne.class)
                || field.isAnnotationPresent(OneToMany.class)
                || field.isAnnotationPresent(ManyToOne.class)
                || field.isAnnotationPresent(ManyToMany.class);
    }
}
