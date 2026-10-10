package com.Marketplace_Management.Shared.Utils.Database;

import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

/**
 * Reads the live database schema. Used by one-time startup data migrations, which must be no-ops
 * once the legacy column they read from has been dropped (Hibernate ddl-auto: update never drops columns).
 */
@Component
public class SchemaInspector {
    private final DSLContext dsl;

    public SchemaInspector(DSLContext dsl) {
        this.dsl = dsl;
    }

    public boolean columnExists(String table, String column) {
        Integer count = dsl.fetchOne(
                "SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = current_schema() AND table_name = ? AND column_name = ?",
                table, column)
                .into(Integer.class);
        return count != null && count > 0;
    }
}
