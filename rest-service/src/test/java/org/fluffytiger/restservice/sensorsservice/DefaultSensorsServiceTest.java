package org.fluffytiger.restservice.sensorsservice;

import org.fluffytiger.restservice.sensors.SensorStatus;
import org.fluffytiger.restservice.sensors.service.DefaultSensorsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@SpringJUnitConfig
class DefaultSensorsServiceTest {
    @Autowired
    DefaultSensorsService service;

    @BeforeEach
    void setUp() {
        this.service = new DefaultSensorsService(
            new MockMeasurementsRepository(),
            new MockSensorStatusRepository()
        );
    }

    @Test
    void test_GetStatus_Empty() {
        var sensorId = UUID.fromString("00000000-c03b-4a04-a25b-38f39be8ebba");
        Assertions.assertEquals(SensorStatus.OK, service.getStatus(sensorId));
    }

    @Test
    void test_GetStatus_3LowAndThen1High() {
        var sensorId = UUID.fromString("00000000-c03b-4a04-a25b-38f39be8ebba");
        insertData(List.of(1000, 2001, 1000, 2001), sensorId);

        Assertions.assertEquals(SensorStatus.WARN, service.getStatus(sensorId));
    }

    @Test
    void test_GetStatus_2High() {
        var sensorId = UUID.fromString("00000000-c03b-4a04-a25b-38f39be8ebba");
        insertData(List.of(2001, 2002), sensorId);

        Assertions.assertEquals(SensorStatus.WARN, service.getStatus(sensorId));
    }

    @Test
    void test_GetStatus_3High() {
        var sensorId = UUID.fromString("00000000-c03b-4a04-a25b-38f39be8ebba");
        insertData(List.of(2001, 2002, 2003), sensorId);

        Assertions.assertEquals(SensorStatus.ALERT, service.getStatus(sensorId));
    }

    @Test
    void test_GetStatus_3HighAndThen1Low() {
        var sensorId = UUID.fromString("00000000-c03b-4a04-a25b-38f39be8ebba");
        insertData(List.of(2001, 2002, 2003, 1000), sensorId);

        Assertions.assertEquals(SensorStatus.ALERT, service.getStatus(sensorId));
    }

    @Test
    void test_GetStatus_3HighAndThen3Low() {
        var sensorId = UUID.fromString("00000000-c03b-4a04-a25b-38f39be8ebba");
        insertData(List.of(2001, 2002, 2003, 1000, 1000, 1000), sensorId);

        Assertions.assertEquals(SensorStatus.OK, service.getStatus(sensorId));
    }

    @Test
    void test_GetStatus_3HighThen3LowThen1High() {
        var sensorId = UUID.fromString("00000000-c03b-4a04-a25b-38f39be8ebba");
        insertData(List.of(2001, 2002, 2003, 1000, 1000, 1000, 2000), sensorId);

        Assertions.assertEquals(SensorStatus.WARN, service.getStatus(sensorId));
    }

    @Test
    void test_GetStatus_3HighThen3LowThen2High() {
        var sensorId = UUID.fromString("00000000-c03b-4a04-a25b-38f39be8ebba");
        insertData(List.of(2001, 2002, 2003, 1000, 1000, 1000, 2000, 2000), sensorId);

        Assertions.assertEquals(SensorStatus.WARN, service.getStatus(sensorId));
    }

    @Test
    void test_GetStatus_4High() {
        var sensorId = UUID.fromString("00000000-c03b-4a04-a25b-38f39be8ebba");
        insertData(List.of(2001, 2002, 2003, 2004), sensorId);

        Assertions.assertEquals(SensorStatus.ALERT, service.getStatus(sensorId));
    }

    @Test
    void test_GetStatus_2Low() {
        var sensorId = UUID.fromString("00000000-c03b-4a04-a25b-38f39be8ebba");
        insertData(List.of(1001, 1002), sensorId);

        Assertions.assertEquals(SensorStatus.OK, service.getStatus(sensorId));
    }

    @Test
    void test_GetStatus_3Low() {
        var sensorId = UUID.fromString("00000000-c03b-4a04-a25b-38f39be8ebba");
        insertData(List.of(1001, 1002, 1003), sensorId);

        Assertions.assertEquals(SensorStatus.OK, service.getStatus(sensorId));
    }

    @Test
    void test_GetStatus_4Low() {
        var sensorId = UUID.fromString("00000000-c03b-4a04-a25b-38f39be8ebba");
        insertData(List.of(1001, 1002, 1003, 1004), sensorId);

        Assertions.assertEquals(SensorStatus.OK, service.getStatus(sensorId));
    }

    @Test
    void test_GetStatus_3HighThen3LowThen1High_atEachGetStatus() {
        var sensorId = UUID.fromString("00000000-c03b-4a04-a25b-38f39be8ebba");
        var now = OffsetDateTime.now();

        service.createMeasurement(2001, now.minusSeconds(7), sensorId);
        Assertions.assertEquals(SensorStatus.WARN, service.getStatus(sensorId));

        service.createMeasurement(2002, now.minusSeconds(6), sensorId);
        Assertions.assertEquals(SensorStatus.WARN, service.getStatus(sensorId));

        service.createMeasurement(2003, now.minusSeconds(5), sensorId);
        Assertions.assertEquals(SensorStatus.ALERT, service.getStatus(sensorId));

        service.createMeasurement(1000, now.minusSeconds(4), sensorId);
        Assertions.assertEquals(SensorStatus.ALERT, service.getStatus(sensorId));

        service.createMeasurement(1000, now.minusSeconds(3), sensorId);
        Assertions.assertEquals(SensorStatus.ALERT, service.getStatus(sensorId));

        service.createMeasurement(1000, now.minusSeconds(2), sensorId);
        Assertions.assertEquals(SensorStatus.OK, service.getStatus(sensorId));

        service.createMeasurement(2000, now.minusSeconds(1), sensorId);
        Assertions.assertEquals(SensorStatus.WARN, service.getStatus(sensorId));
    }

    @Test
    void test_GetMetrics_NonEmptyData() {
        var sensorId = UUID.fromString("00000000-c03b-4a04-a25b-38f39be8ebba");
        var data = List.of(1001, 1002, 1003, 1004, 1005, 1006);
        insertData(data, sensorId);

        var expectedMax = data.stream()
            .max(Integer::compareTo)
            .orElse(0);

        var expectedAvg = data.stream()
            .mapToDouble(Integer::doubleValue)
            .average()
            .orElse(0);

        var metrics = service.getMetrics(sensorId);
        Assertions.assertEquals(expectedMax, metrics.getMaxLast30Days());
        Assertions.assertEquals(expectedAvg, metrics.getAvgLast30Days(), 0.001);
    }

    @Test
    void test_GetMetrics_EmptyData() {
        var sensorId = UUID.fromString("00000000-c03b-4a04-a25b-38f39be8ebba");
        var metrics = service.getMetrics(sensorId);
        Assertions.assertEquals(0, metrics.getMaxLast30Days(), 0.001);
        Assertions.assertEquals(0, metrics.getAvgLast30Days(), 0.001);
    }

    private void insertData(List<Integer> co2, UUID sensorId) {
        var now = OffsetDateTime.now();

        for (int i = 0; i < co2.size(); i++) {
            var offset = co2.size() - i;
            service.createMeasurement(co2.get(i), now.minusSeconds(offset), sensorId);
        }
    }
}
