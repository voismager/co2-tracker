package org.fluffytiger.restservice.sensors;

import org.fluffytiger.restservice.sensors.payload.TimedSensorStatus;

import java.util.Optional;
import java.util.UUID;

public interface SensorStatusRepository {
    /**
     * Insert sensor status record into underlying storage with seconds precision.
     *
     * This method does not guarantee for status record to be immediately written
     * and visible for queries in this repository.
     *
     * @param sensorId id of sensor
     * @param status sensor status with specified timestamp and additional state counters
     */
    void insert(UUID sensorId, TimedSensorStatus status);
    /**
     * Return the last status in timeline for given sensor
     *
     * @param sensorId id of sensor
     * @return the last status wrapped in an {@link Optional}
     * or {@link Optional#empty()} if no status records for sensor is found
     */
    Optional<TimedSensorStatus> getLast(UUID sensorId);
}
