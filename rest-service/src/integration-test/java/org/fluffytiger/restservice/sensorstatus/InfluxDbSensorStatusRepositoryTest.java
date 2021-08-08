package org.fluffytiger.restservice.sensorstatus;

import org.fluffytiger.restservice.AbstractInfluxDbTest;
import org.fluffytiger.restservice.sensors.SensorStatus;
import org.fluffytiger.restservice.sensors.payload.TimedSensorStatus;
import org.fluffytiger.restservice.sensors.repository.InfluxDbSensorStatusRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

public class InfluxDbSensorStatusRepositoryTest extends AbstractInfluxDbTest {
    @Autowired
    private InfluxDbSensorStatusRepository repository;

    @Test
    void test_GetLastAfterInsert() {
        var now = Instant.now();
        now = now.minusNanos(now.getNano());
        UUID sensorId = UUID.fromString("e0001001-c03b-4a04-a25b-38f39be8ebba");
        var record = new TimedSensorStatus(SensorStatus.OK, 0, 0, now);

        repository.insert(sensorId, record);

        await()
            .atMost(10, TimeUnit.SECONDS)
            .until(() -> repository.getLast(sensorId).isPresent());

        var result = repository.getLast(sensorId);
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(result.get(), record);
    }
}
