package org.fluffytiger.restservice.sensorsservice;

import org.fluffytiger.restservice.sensors.SensorStatus;
import org.fluffytiger.restservice.sensors.service.DefaultSensorsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@SpringJUnitConfig(classes = TestConfig.class)
class DefaultSensorsServiceTest {
    @Autowired
    DefaultSensorsService service;

    @Test
    void test_GetStatus_Empty() {
        var sensorId = UUID.fromString("00000000-c03b-4a04-a25b-38f39be8ebba");
        Assertions.assertEquals(SensorStatus.OK, service.getStatus(sensorId));
    }

    @Test
    void test_GetStatus_LastHigherThan2000() {
        var sensorId = UUID.fromString("00000001-c03b-4a04-a25b-38f39be8ebba");
        insertData(List.of(1000, 2001, 1000, 2001), sensorId);

        Assertions.assertEquals(SensorStatus.WARN, service.getStatus(sensorId));
    }

    @Test
    void test_GetStatus_2consecutiveMeasurementsHigherThan2000() {
        var sensorId = UUID.fromString("e0000000-c03b-4a04-a25b-38f39be8ebba");
        insertData(List.of(2001, 2002), sensorId);

        Assertions.assertEquals(SensorStatus.WARN, service.getStatus(sensorId));
    }

    @Test
    void test_GetStatus_3consecutiveMeasurementsHigherThan2000() {
        var sensorId = UUID.fromString("e0000001-c03b-4a04-a25b-38f39be8ebba");
        insertData(List.of(2001, 2002, 2003), sensorId);

        Assertions.assertEquals(SensorStatus.ALERT, service.getStatus(sensorId));
    }

    @Test
    void test_GetStatus_4consecutiveMeasurementsHigherThan2000() {
        var sensorId = UUID.fromString("e0000002-c03b-4a04-a25b-38f39be8ebba");
        insertData(List.of(2001, 2002, 2003, 2004), sensorId);

        Assertions.assertEquals(SensorStatus.ALERT, service.getStatus(sensorId));
    }

    @Test
    void test_GetStatus_2consecutiveMeasurementsLowerThan2000() {
        var sensorId = UUID.fromString("e0000011-c03b-4a04-a25b-38f39be8ebba");
        insertData(List.of(1001, 1002), sensorId);

        Assertions.assertEquals(SensorStatus.OK, service.getStatus(sensorId));
    }

    @Test
    void test_GetStatus_3consecutiveMeasurementsLowerThan2000() {
        var sensorId = UUID.fromString("e0000012-c03b-4a04-a25b-38f39be8ebba");
        insertData(List.of(1001, 1002, 1003), sensorId);

        Assertions.assertEquals(SensorStatus.OK, service.getStatus(sensorId));
    }

    @Test
    void test_GetStatus_4consecutiveMeasurementsLowerThan2000() {
        var sensorId = UUID.fromString("e0000013-c03b-4a04-a25b-38f39be8ebba");
        insertData(List.of(1001, 1002, 1003, 1004), sensorId);

        Assertions.assertEquals(SensorStatus.OK, service.getStatus(sensorId));
    }

    @Test
    void test_GetMetrics_NonEmptyData() {
        var sensorId = UUID.fromString("e0020020-c03b-4a04-a25b-38f39be8ebba");
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
        var sensorId = UUID.fromString("e0020021-c03b-4a04-a25b-38f39be8ebba");
        var metrics = service.getMetrics(sensorId);
        Assertions.assertEquals(0, metrics.getMaxLast30Days(), 0.001);
        Assertions.assertEquals(0, metrics.getAvgLast30Days(), 0.001);
    }

    private void insertData(List<Integer> co2, UUID sensorId) {
        for (var measure : co2) {
            service.createMeasurement(measure, OffsetDateTime.now(), sensorId);
        }
    }
}
