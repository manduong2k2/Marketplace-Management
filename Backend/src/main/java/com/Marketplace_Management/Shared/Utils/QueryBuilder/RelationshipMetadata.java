package com.Marketplace_Management.Shared.Utils.QueryBuilder;

public class RelationshipMetadata {

    private final String fieldName;
    private final Class<?> targetEntity;
    private final RelationshipType type;
    private final String joinTable;
    private final String joinColumn;
    private final String inverseJoinColumn;
    private final String mappedBy;

    public RelationshipMetadata(
            String fieldName,
            Class<?> targetEntity,
            RelationshipType type,
            String joinTable,
            String joinColumn,
            String inverseJoinColumn,
            String mappedBy) {
        this.fieldName = fieldName;
        this.targetEntity = targetEntity;
        this.type = type;
        this.joinTable = joinTable;
        this.joinColumn = joinColumn;
        this.inverseJoinColumn = inverseJoinColumn;
        this.mappedBy = mappedBy;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Class<?> getTargetEntity() {
        return targetEntity;
    }

    public RelationshipType getType() {
        return type;
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
    
    public String getMappedBy() {
        return mappedBy;
    }

    @Override
    public String toString() {
        return "RelationshipMetadata{" +
                "fieldName='" + fieldName + '\'' +
                ", targetEntity=" + targetEntity.getSimpleName() +
                ", type=" + type +
                ", joinColumn='" + joinColumn + '\'' +
                ", mappedBy='" + mappedBy + '\'' +
                '}'+ "\n";
    }
}
