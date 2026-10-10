package com.Marketplace_Management.Shared;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.Marketplace_Management.Shared.Utils.Helpers.UuidV7;

class UuidV7Test {

    @Test
    void isVersion7_withRfcVariant_andCurrentTimestamp() {
        long before = System.currentTimeMillis();
        UUID uuid = UuidV7.generate();
        long after = System.currentTimeMillis();

        assertEquals(7, uuid.version());
        assertEquals(2, uuid.variant()); // IETF variant (10xx)
        long timestamp = uuid.getMostSignificantBits() >>> 16;
        assertTrue(timestamp >= before && timestamp <= after);
    }

    @Test
    void laterIds_sortAfterEarlierOnes_andAreUnique() throws InterruptedException {
        List<UUID> ids = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ids.add(UuidV7.generate());
            Thread.sleep(2);
        }
        for (int i = 1; i < ids.size(); i++) {
            // Same order as PostgreSQL's uuid comparison (unsigned, byte by byte)
            assertTrue(ids.get(i - 1).toString().compareTo(ids.get(i).toString()) < 0);
        }

        HashSet<UUID> many = new HashSet<>();
        for (int i = 0; i < 10_000; i++) {
            many.add(UuidV7.generate());
        }
        assertEquals(10_000, many.size());
    }
}
