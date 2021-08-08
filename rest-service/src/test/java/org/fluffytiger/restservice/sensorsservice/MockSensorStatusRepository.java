package org.fluffytiger.restservice.sensorsservice;

import org.fluffytiger.restservice.sensors.SensorStatusRepository;
import org.fluffytiger.restservice.sensors.payload.TimedSensorStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class MockSensorStatusRepository implements SensorStatusRepository {
    private final Map<UUID, TimedSensorStatus> records = new HashMap<>();

    @Override
    public void insert(UUID sensorId, TimedSensorStatus status) {
        records.put(sensorId, status);
    }

    @Override
    public Optional<TimedSensorStatus> getLast(UUID sensorId) {
        return Optional.ofNullable(records.get(sensorId));
    }
}
