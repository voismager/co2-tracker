package org.fluffytiger.restservice.measurements;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import org.fluffytiger.restservice.sensors.repository.InfluxDbMeasurementsRepository;
import org.junit.ClassRule;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest
@SpringJUnitConfig(classes = AbstractInfluxDbTest.Initializer.class)
abstract class AbstractInfluxDbTest {
    static final int EXPOSED_PORT = 8086;
    static final String BUCKET_NAME = "test_bucket";
    static final String ORG_NAME = "test_org";
    private static final String IMAGE_NAME = "influxdb:2.0.7";

    @Container
    @ClassRule
    public static GenericContainer db = new GenericContainer(DockerImageName.parse(IMAGE_NAME))
        .withExposedPorts(EXPOSED_PORT)
        .withEnv("DOCKER_INFLUXDB_INIT_MODE", "setup")
        .withEnv("DOCKER_INFLUXDB_INIT_PASSWORD", "password")
        .withEnv("DOCKER_INFLUXDB_INIT_USERNAME", "username")
        .withEnv("DOCKER_INFLUXDB_INIT_ORG", "test_org")
        .withEnv("DOCKER_INFLUXDB_INIT_BUCKET", "test_bucket")
        .withEnv("DOCKER_INFLUXDB_INIT_ADMIN_TOKEN", "admin_token")
        .withReuse(true);

    private static String getDbUrl() {
        return "http://" + db.getHost() + ":" + db.getMappedPort(EXPOSED_PORT);
    }

    static class Initializer {
        @Bean
        public InfluxDBClient testClient() {
            return InfluxDBClientFactory.create(
                getDbUrl(),
                "admin_token".toCharArray(),
                ORG_NAME,
                BUCKET_NAME
            );
        }

        @Bean
        public InfluxDbMeasurementsRepository measurementsRepository(InfluxDBClient influxDB) {
            return new InfluxDbMeasurementsRepository(influxDB, BUCKET_NAME);
        }
    }
}
