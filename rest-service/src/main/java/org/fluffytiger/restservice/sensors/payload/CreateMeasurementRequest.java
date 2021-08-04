package org.fluffytiger.restservice.sensors.payload;

import java.time.OffsetDateTime;

public class CreateMeasurementRequest {
    private final Integer co2;
    private final OffsetDateTime time;

    public CreateMeasurementRequest(Integer co2, OffsetDateTime time) {
        this.co2 = co2;
        this.time = time;
    }

    public Integer getCo2() {
        return co2;
    }

    public OffsetDateTime getTime() {
        return time;
    }
}
