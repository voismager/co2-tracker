package org.fluffytiger.restservice.sensors.payload;

import java.time.Instant;
import java.util.List;

public class LastMeasurements {
    public static final LastMeasurements EMPTY = new LastMeasurements(List.of(), Instant.EPOCH);

    private final List<Integer> values;
    private final Instant lastValueAt;

    public LastMeasurements(List<Integer> values, Instant lastValueAt) {
        this.values = values;
        this.lastValueAt = lastValueAt;
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public List<Integer> getValues() {
        return values;
    }

    public Instant getLastValueAt() {
        return lastValueAt;
    }
}
