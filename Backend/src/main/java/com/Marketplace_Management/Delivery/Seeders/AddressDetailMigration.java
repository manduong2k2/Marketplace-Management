package com.Marketplace_Management.Delivery.Seeders;

import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.Marketplace_Management.Shared.Utils.Database.SchemaInspector;

/**
 * One-time data move: an address used to be house_number + street_name + detail (+ title);
 * it is now a single "detail" line. Makes the legacy columns nullable (new rows no longer fill them)
 * and merges them into detail. Idempotent: each step can be re-run safely, and the whole runner is
 * a no-op once street_name has been dropped.
 * Hibernate (ddl-auto: update) never drops columns, so drop them manually when convenient:
 *   ALTER TABLE addresses DROP COLUMN title, DROP COLUMN street_name, DROP COLUMN house_number;
 */
@Component
public class AddressDetailMigration implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(AddressDetailMigration.class);

    private final DSLContext dsl;
    private final SchemaInspector schema;

    public AddressDetailMigration(DSLContext dsl, SchemaInspector schema) {
        this.dsl = dsl;
        this.schema = schema;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!schema.columnExists("addresses", "street_name")) {
            return;
        }

        // First: new rows leave these columns empty, and the merge below clears them
        dsl.execute("ALTER TABLE addresses ALTER COLUMN street_name DROP NOT NULL");
        dsl.execute("ALTER TABLE addresses ALTER COLUMN house_number DROP NOT NULL");

        // "12" + "Nguyen Trai" + "Floor 3" -> "12, Nguyen Trai, Floor 3" (empty parts skipped).
        // Single statement, and merged rows no longer match the WHERE, so a re-run never merges twice.
        int merged = dsl.execute("""
                UPDATE addresses
                SET detail = concat_ws(', ', NULLIF(trim(house_number), ''), NULLIF(trim(street_name), ''), NULLIF(trim(detail), '')),
                    house_number = NULL,
                    street_name = NULL
                WHERE house_number IS NOT NULL OR street_name IS NOT NULL
                """);
        if (merged > 0) {
            logger.info("Merged house number/street name into detail for {} address(es)", merged);
        }
    }
}
