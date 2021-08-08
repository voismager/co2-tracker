package org.fluffytiger.restservice.sensors;

import org.fluffytiger.restservice.sensors.payload.GetMetricsResponse;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface SensorsService {
    /**
     * Save measurement in underlying storage. The measurement is stored with seconds precision.
     * Note that this method does not guarantee for record to be immediately accessible
     * for queries.
     *
     * @param co2 CO2 level measurement
     * @param time time of measurement
     * @param sensor id of sensor where the measurement was performed
     */
    void createMeasurement(Integer co2, OffsetDateTime time, UUID sensor);
    /**
     * Return status of specified sensor.
     *
     * The status is determined by the following rules:
     *     - If no measurements for sensor were acquired, {@link SensorStatus#OK}
     *     - If the last 3 or more consecutive measurements were less than 2000, {@link SensorStatus#OK}
     *     - If there were 3 or more consecutive measurements higher or equal to 2000
     *       not followed at any point by 3 or more consecutive
     *       measurements less than 2000, {@link SensorStatus#ALERT}
     *     - Otherwise, {@link SensorStatus#WARN}
     *
     * @param sensor id of sensor
     * @return status of sensor
     */
    SensorStatus getStatus(UUID sensor);
    /**
     * @param sensor id of sensor
     * @return metrics from last 30 days for specified sensor
     */
    GetMetricsResponse getMetrics(UUID sensor);
}
