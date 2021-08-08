package org.fluffytiger.restservice.sensorsservice;

import org.fluffytiger.restservice.sensors.Co2MeasurementsRepository;
import org.fluffytiger.restservice.sensors.MeasureRecord;
import org.fluffytiger.restservice.sensors.payload.LastMeasurements;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

class MockMeasurementsRepository implements Co2MeasurementsRepository {
    private final List<MeasureRecord> records = new ArrayList<>();

    @Override
    public void insertRecord(MeasureRecord record) {
        var recordWithSec = new MeasureRecord(
            record.getCo2(),
            record.getDate().minusNanos(record.getDate().getNano()),
            record.getSensorId()
        );

        records.add(recordWithSec);
    }

    @Override
    public Integer getMaxOverLastNDays(UUID sensorId, int days) {
        OffsetDateTime now = OffsetDateTime.now();

        return records.stream()
            .filter(record -> record.getSensorId().equals(sensorId))
            .filter(record -> record.getDate().isAfter(now.minusDays(days)))
            .map(MeasureRecord::getCo2)
            .reduce(Integer::max)
            .orElse(0);
    }

    @Override
    public Double getAverageOverLastNDays(UUID sensorId, int days) {
        OffsetDateTime now = OffsetDateTime.now();

        return records.stream()
            .filter(record -> record.getSensorId().equals(sensorId))
            .filter(record -> record.getDate().isAfter(now.minusDays(days)))
            .mapToDouble(record -> record.getCo2().doubleValue())
            .average()
            .orElse(0);
    }

    @Override
    public LastMeasurements getMeasurements(UUID sensorId, Instant start) {
        var records = this.records.stream()
            .filter(record -> record.getDate().toInstant().isAfter(start) || record.getDate().toInstant().equals(start))
            .filter(record -> record.getSensorId().equals(sensorId))
            .collect(Collectors.toList());

        if (!records.isEmpty()) {
            return new LastMeasurements(
                records.stream().map(MeasureRecord::getCo2).collect(Collectors.toList()),
                records.get(records.size() - 1).getDate().toInstant());
        } else {
            return LastMeasurements.EMPTY;
        }
    }
}
