package org.fluffytiger.restservice.sensors.payload;

public class GetMetricsResponse {
    private final Integer maxLast30Days;
    private final Double avgLast30Days;

    public GetMetricsResponse(Integer maxLast30Days, Double avgLast30Days) {
        this.maxLast30Days = maxLast30Days;
        this.avgLast30Days = avgLast30Days;
    }

    public Integer getMaxLast30Days() {
        return maxLast30Days;
    }

    public Double getAvgLast30Days() {
        return avgLast30Days;
    }
}
