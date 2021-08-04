package org.fluffytiger.restservice.sensors;

import org.fluffytiger.restservice.sensors.payload.CreateMeasurementRequest;
import org.fluffytiger.restservice.sensors.payload.GetMetricsResponse;
import org.fluffytiger.restservice.sensors.payload.GetStatusResponse;
import org.fluffytiger.restservice.sensors.service.DefaultSensorsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(path = "${paths.api.v1}/sensors")
public class SensorsEndpoint {
    private final DefaultSensorsService defaultSensorsService;

    public SensorsEndpoint(DefaultSensorsService defaultSensorsService) {
        this.defaultSensorsService = defaultSensorsService;
    }

    @RequestMapping(path = "/{uuid}/measurements", method = RequestMethod.POST)
    public ResponseEntity<?> createMeasurement(
        @PathVariable("uuid") UUID id,
        @RequestBody CreateMeasurementRequest request
    ) {
        this.defaultSensorsService.createMeasurement(request.getCo2(), request.getTime(), id);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(path = "/{uuid}", method = RequestMethod.GET)
    public ResponseEntity<GetStatusResponse> getStatus(@PathVariable("uuid") UUID id) {
        return ResponseEntity.ok(new GetStatusResponse(this.defaultSensorsService.getStatus(id)));
    }

    @RequestMapping(path = "/{uuid}/metrics", method = RequestMethod.GET)
    public ResponseEntity<GetMetricsResponse> getMetrics(@PathVariable("uuid") UUID id) {
        return ResponseEntity.ok(this.defaultSensorsService.getMetrics(id));
    }
}
