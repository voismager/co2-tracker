package org.fluffytiger.restservice.sensors;

import org.fluffytiger.restservice.sensors.payload.LastMeasurements;

import java.time.Instant;
import java.util.UUID;

public interface Co2MeasurementsRepository {
    /**
     * Insert measure record into underlying storage with seconds precision.
     * This method does not guarantee for record to be immediately written
     * and visible for queries.
     *
     * @param record record to insert
     */
    void insertRecord(MeasureRecord record);
    /**
     * @param sensorId id of specified sensor
     * @param days number of days to calculate value over
     *
     * @return max measured value for specified sensor over the last
     * specified number of days.
     */
    Integer getMaxOverLastNDays(UUID sensorId, int days);
    /**
     * @param sensorId id of specified sensor
     * @param days number of days to calculate value over
     *
     * @return average measured value for specified sensor over the last
     * specified number of days.
     */
    Double getAverageOverLastNDays(UUID sensorId, int days);
    /**
     * @param sensorId id of specified sensor
     * @param start the earliest time point to include in results
     *
     * @return list of last co2 measurements for specified sensor,
     * sorted ascending by time
     */
    LastMeasurements getMeasurements(UUID sensorId, Instant start);
}
