package com.Marketplace_Management.Shared.Validation.Rules;

import java.util.UUID;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import com.Marketplace_Management.Shared.Annotation.Rules.Unique;

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
    private String except;

    @Override
    public void initialize(Unique unique) {
        this.table = unique.table();
        this.column = unique.column();
        this.type = unique.type();
        this.deletedAtColumn = unique.deletedAtColumn();
        this.whereClause = unique.whereClause();
        this.except = unique.except();
        this.when = unique.when();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (!when) {
            return true;
        }

        if (value == null)
            return true;

        if (type == String.class) {
            value = value.toString();
        }

        if (type == UUID.class) {
            value = UUID.fromString(value.toString());
        }

        String sql = "SELECT COUNT(*) FROM " + table + " WHERE " + column + " = :value";

        if (deletedAtColumn != null && !deletedAtColumn.isBlank()) {
            sql += " AND " + deletedAtColumn + " IS NULL";
        }

        if (whereClause != null && !whereClause.isBlank()) {
            sql += " AND (" + whereClause + ")";
        }

        var query = em.createNativeQuery(sql)
                .setParameter("value", value);

        if (!except.isBlank()) {
            query.setParameter("except", except);
        }

        Number count = (Number) query.getSingleResult();

        return count.intValue() == 0;
    }
}
