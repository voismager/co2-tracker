package org.fluffytiger.restservice.sensors.repository;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApi;
import com.influxdb.client.WriteOptions;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.dsl.Flux;
import com.influxdb.query.dsl.functions.restriction.Restrictions;
import org.fluffytiger.restservice.sensors.SensorStatus;
import org.fluffytiger.restservice.sensors.SensorStatusRepository;
import org.fluffytiger.restservice.sensors.payload.TimedSensorStatus;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class InfluxDbSensorStatusRepository implements SensorStatusRepository {
    private final WriteApi writeApi;
    private final QueryApi queryApi;
    private final String bucketName;

    public InfluxDbSensorStatusRepository(InfluxDBClient influxDB, String bucketName) {
        this.writeApi = influxDB.getWriteApi(WriteOptions.DEFAULTS);
        this.queryApi = influxDB.getQueryApi();
        this.bucketName = bucketName;
    }

    @Override
    public void insert(UUID sensorId, TimedSensorStatus status) {
        var state = String.join(
            ",",
            status.getStatus().name(), status.getLowCounter().toString(), status.getHighCounter().toString()
        );

        Point point = Point.measurement("status")
            .addTag("sensor", sensorId.toString())
            .addField("state", state)
            .time(status.getUpdatedAt().atOffset(ZoneOffset.UTC).toEpochSecond(), WritePrecision.S);

        writeApi.writePoint(point);
    }

    @Override
    public Optional<TimedSensorStatus> getLast(UUID sensorId) {
        var query = Flux.from(bucketName)
            .range(Instant.EPOCH, Instant.now())
            .filter(Restrictions.and(
                Restrictions.measurement().equal("status"),
                Restrictions.tag("sensor").equal(sensorId.toString())
            ))
            .last()
            .keep(List.of("_time", "_value"))
            .toString();

        var result = this.queryApi.query(query);

        if (result.isEmpty()) {
            return Optional.empty();
        } else {
            var record = result.get(0).getRecords().get(0);
            var time = record.getTime();
            var state = ((String) record.getValue()).split(",");

            return Optional.of(new TimedSensorStatus(
                SensorStatus.valueOf(state[0]),
                Integer.valueOf(state[1]),
                Integer.valueOf(state[2]),
                time
            ));
        }
    }
}
