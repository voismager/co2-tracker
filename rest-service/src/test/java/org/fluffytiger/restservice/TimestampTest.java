package org.fluffytiger.restservice;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

class TimestampTest {
    @Test
    void test_OffsetDateTimeToTimestamp() {
        OffsetDateTime date = OffsetDateTime.parse("2021-08-07T18:55:47+00:00");
        Timestamp timestamp = Timestamp.valueOf(date.atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime());
        Assertions.assertEquals(date.toLocalDateTime(), timestamp.toLocalDateTime());
    }
}
