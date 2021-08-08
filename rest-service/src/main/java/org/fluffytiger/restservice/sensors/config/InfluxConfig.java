package org.fluffytiger.restservice.sensors.config;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import org.fluffytiger.restservice.sensors.Co2MeasurementsRepository;
import org.fluffytiger.restservice.sensors.SensorStatusRepository;
import org.fluffytiger.restservice.sensors.repository.InfluxDbMeasurementsRepository;
import org.fluffytiger.restservice.sensors.repository.InfluxDbSensorStatusRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfluxConfig {
    private final InfluxProperties properties;

    public InfluxConfig(InfluxProperties properties) {
        this.properties = properties;
    }

    @Bean
    public InfluxDBClient influxDBClient() {
        return InfluxDBClientFactory.create(
            properties.getUrl(),
            properties.getToken().toCharArray(),
            properties.getOrg(),
            properties.getBucket()
        );
    }

    @Bean
    public Co2MeasurementsRepository co2MeasurementsRepository(InfluxDBClient influxDB) {
        return new InfluxDbMeasurementsRepository(influxDB, properties.getBucket());
    }

    @Bean
    public SensorStatusRepository sensorStatusRepository(InfluxDBClient influxDB) {
        return new InfluxDbSensorStatusRepository(influxDB, properties.getBucket());
    }
}
