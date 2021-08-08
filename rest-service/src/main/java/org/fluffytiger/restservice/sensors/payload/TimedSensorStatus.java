package org.fluffytiger.restservice.sensors.payload;

import org.fluffytiger.restservice.sensors.SensorStatus;

import java.time.Instant;
import java.util.Objects;

public class TimedSensorStatus {
    private final SensorStatus status;
    private final Integer lowCounter;
    private final Integer highCounter;
    private final Instant updatedAt;

    public TimedSensorStatus(SensorStatus status, Integer lowCounter, Integer highCounter, Instant updatedAt) {
        this.status = status;
        this.lowCounter = lowCounter;
        this.highCounter = highCounter;
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TimedSensorStatus that = (TimedSensorStatus) o;
        return status == that.status &&
            Objects.equals(lowCounter, that.lowCounter) &&
            Objects.equals(highCounter, that.highCounter) &&
            Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, lowCounter, highCounter, updatedAt);
    }

    @Override
    public String toString() {
        return "TimedSensorStatus{" +
            "status=" + status +
            ", lowCounter=" + lowCounter +
            ", highCounter=" + highCounter +
            ", updatedAt=" + updatedAt +
            '}';
    }

    public SensorStatus getStatus() {
        return status;
    }

    public Integer getLowCounter() {
        return lowCounter;
    }

    public Integer getHighCounter() {
        return highCounter;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
