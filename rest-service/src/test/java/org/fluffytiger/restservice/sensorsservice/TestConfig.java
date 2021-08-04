package org.fluffytiger.restservice.sensorsservice;

import org.fluffytiger.restservice.sensors.service.DefaultSensorsService;
import org.springframework.context.annotation.Bean;

class TestConfig {
    @Bean("default")
    DefaultSensorsService defaultSensorsService() {
        return new DefaultSensorsService(new MockMeasurementsRepository());
    }
}
