package org.fluffytiger.restservice.sensors;

import java.time.OffsetDateTime;
import java.util.UUID;

public class MeasureRecord {
    private final Integer co2;
    private final OffsetDateTime date;
    private final UUID sensorId;

    public MeasureRecord(Integer co2, OffsetDateTime date, UUID sensorId) {
        this.co2 = co2;
        this.date = date;
        this.sensorId = sensorId;
    }

    public Integer getCo2() {
        return co2;
    }

    public OffsetDateTime getDate() {
        return date;
    }

    public UUID getSensorId() {
        return sensorId;
    }
}
