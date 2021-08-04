package org.fluffytiger.restservice.sensors;

import java.util.List;
import java.util.UUID;

public interface Co2MeasurementsRepository {
    /**
     * Insert measure record into underlying storage.
     * This method does not guarantee for record to be immediately written
     * and visible for queries.
     * Please refer to {@link Co2MeasurementsRepository#isAsync()} to determine the exact behaviour.
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
     * @param limit number of maximum retrieved measurements
     *
     * @return list of last co2 measurements for specified sensor,
     * sorted descending by time
     */
    List<Integer> getLastMeasurements(UUID sensorId, int limit);
    /**
     * Determine whether the result of insert operation will be
     * visible immediately.
     */
    boolean isAsync();
}
