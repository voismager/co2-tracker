package org.fluffytiger.restservice.sensors.service;

import org.fluffytiger.restservice.sensors.Co2MeasurementsRepository;
import org.fluffytiger.restservice.sensors.MeasureRecord;
import org.fluffytiger.restservice.sensors.SensorStatus;
import org.fluffytiger.restservice.sensors.SensorStatusRepository;
import org.fluffytiger.restservice.sensors.SensorsService;
import org.fluffytiger.restservice.sensors.payload.GetMetricsResponse;
import org.fluffytiger.restservice.sensors.payload.LastMeasurements;
import org.fluffytiger.restservice.sensors.payload.TimedSensorStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.fluffytiger.restservice.sensors.SensorStatus.ALERT;
import static org.fluffytiger.restservice.sensors.SensorStatus.OK;
import static org.fluffytiger.restservice.sensors.SensorStatus.WARN;

@Service
public class DefaultSensorsService implements SensorsService {
    private static final TimedSensorStatus DEFAULT = new TimedSensorStatus(OK, 0, 0, Instant.EPOCH);

    private final Co2MeasurementsRepository measurements;
    private final SensorStatusRepository statuses;

    public DefaultSensorsService(Co2MeasurementsRepository measurements, SensorStatusRepository statuses) {
        this.measurements = measurements;
        this.statuses = statuses;
    }

    @Override
    public void createMeasurement(Integer co2, OffsetDateTime time, UUID sensor) {
        measurements.insertRecord(new MeasureRecord(co2, time, sensor));
    }

    @Override
    public GetMetricsResponse getMetrics(UUID sensor) {
        int days = 30;
        return new GetMetricsResponse(
            this.measurements.getMaxOverLastNDays(sensor, days),
            this.measurements.getAverageOverLastNDays(sensor, days)
        );
    }

    @Override
    public SensorStatus getStatus(UUID sensor) {
        var lastStatus = this.statuses.getLast(sensor).orElse(DEFAULT);

        var recordsAfterLastStatus = this.measurements.getMeasurements(
            sensor,
            lastStatus.getUpdatedAt().plusSeconds(1)
        );

        if (recordsAfterLastStatus.isEmpty()) {
            return lastStatus.getStatus();
        } else {
            var newStatus = calculateStatus(lastStatus, recordsAfterLastStatus);
            this.statuses.insert(sensor, newStatus);
            return newStatus.getStatus();
        }
    }

    private static boolean isSafe(int value) {
        return value < 2000;
    }

    private static TimedSensorStatus calculateStatus(TimedSensorStatus currStatus, LastMeasurements last) {
        if (last.isEmpty()) return currStatus;

        var lowCounter = currStatus.getLowCounter();
        var highCounter = currStatus.getHighCounter();
        var status = currStatus.getStatus();

        for (var record : last.getValues()) {
            if (status == OK) {
                status = isSafe(record) ? OK : WARN;
            } else if (status == ALERT) {
                status = (lowCounter >= 2 && isSafe(record)) ? OK : ALERT;
            } else if (status == WARN) {
                if (!isSafe(record)) {
                    status = highCounter >= 2 ? ALERT : WARN;
                } else {
                    status = OK;
                }
            }

            if (isSafe(record)) {
                ++lowCounter;
                highCounter = 0;
            } else {
                ++highCounter;
                lowCounter = 0;
            }
        }

        return new TimedSensorStatus(status, lowCounter, highCounter, last.getLastValueAt());
    }
}
