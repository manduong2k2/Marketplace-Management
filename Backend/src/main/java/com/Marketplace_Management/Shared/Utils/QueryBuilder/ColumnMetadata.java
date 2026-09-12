package com.Marketplace_Management.Shared.Utils.QueryBuilder;

public class ColumnMetadata {

    private final String fieldName;
    private final String columnName;
    private final Class<?> javaType;

    public ColumnMetadata(
            String fieldName,
            String columnName,
            Class<?> javaType) {
        this.fieldName = fieldName;
        this.columnName = columnName;
        this.javaType = javaType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getColumnName() {
        return columnName;
    }

    public Class<?> getJavaType() {
        return javaType;
    }

    @Override
    public String toString() {
        return "ColumnMetadata{" +
                "fieldName='" + fieldName + '\'' +
                ", columnName='" + columnName + '\'' +
                ", javaType=" + javaType.getSimpleName() +
                '}';
    }
}
