package com.Marketplace_Management.Auth.Seeders;

import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.Marketplace_Management.Shared.Utils.Database.SchemaInspector;

/**
 * One-time data move: Google links used to live in users.google_id; they now live in oauth_infos.
 * Copies every users.google_id that has no oauth_infos row yet. Idempotent (safe on every start),
 * and a no-op once the users.google_id column has been dropped.
 * Hibernate (ddl-auto: update) never drops columns, so drop it manually when convenient:
 *   ALTER TABLE users DROP COLUMN google_id;
 */
@Component
public class OAuthInfoMigration implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(OAuthInfoMigration.class);

    private final DSLContext dsl;
    private final SchemaInspector schema;

    public OAuthInfoMigration(DSLContext dsl, SchemaInspector schema) {
        this.dsl = dsl;
        this.schema = schema;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!schema.columnExists("users", "google_id")) {
            return;
        }

        int copied = dsl.execute("""
                INSERT INTO oauth_infos (id, user_id, oauth_provider, oauth_provider_subject, created_at)
                SELECT gen_random_uuid(), u.id, 'GOOGLE', u.google_id, now()
                FROM users u
                WHERE u.google_id IS NOT NULL
                  AND NOT EXISTS (
                      SELECT 1 FROM oauth_infos o
                      WHERE o.oauth_provider = 'GOOGLE' AND o.oauth_provider_subject = u.google_id)
                """);
        if (copied > 0) {
            logger.info("Moved {} Google account link(s) from users.google_id to oauth_infos", copied);
        }
    }
}
