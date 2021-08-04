package org.fluffytiger.restservice.sensors;

import org.fluffytiger.restservice.sensors.payload.GetMetricsResponse;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface SensorsService {
    /**
     * Save measurement in underlying storage.
     * Note that this method does not guarantee for record to be immediately accessible
     * for queries.
     *
     * @param co2 CO2 level measurement
     * @param time time of measurement
     * @param sensor id of sensor where the measurement was performed
     */
    void createMeasurement(Integer co2, OffsetDateTime time, UUID sensor);
    /**
     * @param sensor id of sensor
     * @return
     *  {@link SensorStatus#ALERT} if the last 3 or more consecutive measurements were higher than 2000
     *  {@link SensorStatus#WARN} if the last measurement were higher than 2000
     *  and the last 3 or more consecutive measurements were not higher than 2000
     *  {@link SensorStatus#OK} otherwise
     */
    SensorStatus getStatus(UUID sensor);

    /**
     * @param sensor id of sensor
     * @return metrics for specified sensor
     */
    GetMetricsResponse getMetrics(UUID sensor);
}
