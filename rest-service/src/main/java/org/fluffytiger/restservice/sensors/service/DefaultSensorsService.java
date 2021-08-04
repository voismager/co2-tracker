package org.fluffytiger.restservice.sensors.service;

import org.fluffytiger.restservice.sensors.MeasureRecord;
import org.fluffytiger.restservice.sensors.Co2MeasurementsRepository;
import org.fluffytiger.restservice.sensors.SensorStatus;
import org.fluffytiger.restservice.sensors.SensorsService;
import org.fluffytiger.restservice.sensors.payload.GetMetricsResponse;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class DefaultSensorsService implements SensorsService {
    private final Co2MeasurementsRepository measurements;

    public DefaultSensorsService(Co2MeasurementsRepository measurements) {
        this.measurements = measurements;
    }

    @Override
    public void createMeasurement(Integer co2, OffsetDateTime time, UUID sensor) {
        this.measurements.insertRecord(new MeasureRecord(co2, time, sensor));
    }

    @Override
    public SensorStatus getStatus(UUID sensor) {
        var lastRecords = this.measurements.getLastMeasurements(sensor, 3, 1);

        if (lastRecords.isEmpty()) return SensorStatus.OK;

        if (lastRecords.size() == 3) {
            if (allGreaterThan(lastRecords, 2000)) return SensorStatus.ALERT;
            if (allLessThan(lastRecords, 2000)) return SensorStatus.OK;
        }

        if (lastRecords.get(0) > 2000) {
            return SensorStatus.WARN;
        } else {
            return SensorStatus.OK;
        }
    }

    @Override
    public GetMetricsResponse getMetrics(UUID sensor) {
        int days = 30;
        return new GetMetricsResponse(
            this.measurements.getMaxOverLastNDays(sensor, days),
            this.measurements.getAverageOverLastNDays(sensor, days)
        );
    }

    private static boolean allGreaterThan(Iterable<Integer> numbers, int value) {
        for (var num : numbers) {
            if (num <= value) return false;
        }

        return true;
    }

    private static boolean allLessThan(Iterable<Integer> numbers, int value) {
        for (var num : numbers) {
            if (num >= value) return false;
        }

        return true;
    }
}
