package org.fluffytiger.restservice.sensors.payload;

import org.fluffytiger.restservice.sensors.SensorStatus;

public class GetStatusResponse {
    private final SensorStatus status;

    public GetStatusResponse(SensorStatus status) {
        this.status = status;
    }

    public SensorStatus getStatus() {
        return status;
    }
}
