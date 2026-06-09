package com.StoreManagement.Shared.Application.Validator.Rules;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.StoreManagement.Shared.Application.Annotation.Rules.Exist;

@Component
public class ExistValidator implements ConstraintValidator<Exist, Object> {

    @PersistenceContext
    private EntityManager em;

    private String table;
    private String column;
    private Class<?> type;
    private String deletedAtColumn;
    private String whereClause;

    @Override
    public void initialize(Exist exist) {
        this.table = exist.table();
        this.column = exist.column();
        this.type = exist.type();
        this.deletedAtColumn = exist.deletedAtColumn();
        this.whereClause = exist.whereClause();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;

        if(type == UUID.class) {
            value = UUID.fromString(value.toString());
        }

        if(type == String.class) {
            value = value.toString();
        }

        String sql = "SELECT COUNT(*) FROM " + table + " WHERE " + column + " = :value";

        if (deletedAtColumn != null && !deletedAtColumn.isBlank()) {
            sql += " AND " + deletedAtColumn + " IS NULL";
        }
        
        if (whereClause != null && !whereClause.isBlank()) {
            sql += " AND (" + whereClause + ")";
        }

        Number count = (Number) em.createNativeQuery(sql)
                .setParameter("value", value)
                .getSingleResult();

        return count.intValue() != 0;
    }
}
