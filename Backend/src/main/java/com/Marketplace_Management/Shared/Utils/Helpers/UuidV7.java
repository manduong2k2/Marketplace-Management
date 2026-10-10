package com.Marketplace_Management.Shared.Utils.Helpers;

import java.security.SecureRandom;
import java.util.UUID;

/**
 * UUID version 7 (RFC 9562): 48-bit Unix time in milliseconds, then random bits. Ids created later sort
 * after earlier ones, so B-tree indexes stay compact (inserts go to the end instead of random pages).
 * Entity ids use Hibernate's @UuidGenerator(style = VERSION_7); this is for ids created in code (token jti, event id).
 * Not for values that must not reveal their creation time (use UUID.randomUUID(), v4, for those).
 */
public final class UuidV7 {
    private static final SecureRandom RANDOM = new SecureRandom();

    private UuidV7() {
    }

    public static UUID generate() {
        long timestamp = System.currentTimeMillis();
        long randA = RANDOM.nextInt(1 << 12);   // 12 bits
        long randB = RANDOM.nextLong();         // 62 bits used

        long msb = (timestamp << 16)            // unix_ts_ms: 48 bits
                | (0x7L << 12)                  // version 7
                | randA;
        long lsb = (randB & 0x3FFFFFFFFFFFFFFFL)
                | 0x8000000000000000L;          // variant 10xx (RFC 9562)
        return new UUID(msb, lsb);
    }
}
