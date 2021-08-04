package org.fluffytiger.restservice.sensorsservice;

import org.fluffytiger.restservice.sensors.Co2MeasurementsRepository;
import org.fluffytiger.restservice.sensors.MeasureRecord;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

class MockMeasurementsRepository implements Co2MeasurementsRepository {
    private final Deque<MeasureRecord> records = new LinkedList<>();

    @Override
    public void insertRecord(MeasureRecord record) {
        records.addFirst(record);
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
    public List<Integer> getLastMeasurements(UUID sensorId, int limit) {
        return records.stream()
            .filter(record -> record.getSensorId().equals(sensorId))
            .map(MeasureRecord::getCo2)
            .limit(limit)
            .collect(toListReversed());
    }

    public static <T> Collector<T, ?, List<T>> toListReversed() {
        return Collectors.collectingAndThen(Collectors.toList(), l -> {
            Collections.reverse(l);
            return l;
        });
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
