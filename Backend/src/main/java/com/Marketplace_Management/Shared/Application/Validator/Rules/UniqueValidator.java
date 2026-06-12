package com.Marketplace_Management.Shared.Application.Validator.Rules;

import java.util.UUID;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import com.Marketplace_Management.Shared.Application.Annotation.Rules.Unique;

@Component
public class UniqueValidator implements ConstraintValidator<Unique, Object> {

    @PersistenceContext
    private EntityManager em;

    private boolean when;
    private String table;
    private String column;
    private Class<?> type;
    private String deletedAtColumn;
    private String whereClause;

    @Override
    public void initialize(Unique unique) {
        this.table = unique.table();
        this.column = unique.column();
        this.type = unique.type();
        this.deletedAtColumn = unique.deletedAtColumn();
        this.whereClause = unique.whereClause();
        this.when = unique.when();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if(!when) {
            return true;
        }

        if (value == null) return true;
        
        if(type == String.class) {
            value = value.toString();
        }

        if(type == UUID.class) {
            value = UUID.fromString(value.toString());
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

        return count.intValue() == 0;
    }
}
