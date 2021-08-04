package org.fluffytiger.restservice.sensors.repository;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApi;
import com.influxdb.client.WriteOptions;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.dsl.Flux;
import com.influxdb.query.dsl.functions.restriction.Restrictions;
import org.fluffytiger.restservice.sensors.Co2MeasurementsRepository;
import org.fluffytiger.restservice.sensors.MeasureRecord;

import javax.annotation.PreDestroy;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class InfluxDbMeasurementsRepository implements Co2MeasurementsRepository {
    private final InfluxDBClient influxDB;
    private final WriteApi writeApi;
    private final String bucketName;

    public InfluxDbMeasurementsRepository(InfluxDBClient influxDB, String bucketName) {
        this.influxDB = influxDB;
        this.writeApi = influxDB.getWriteApi(WriteOptions
            .builder()
            .batchSize(10000)
            .bufferLimit(50000)
            .build()
        );
        this.bucketName = bucketName;
    }

    @PreDestroy
    public void cleanup() {
        influxDB.close();
    }

    @Override
    public void insertRecord(MeasureRecord record) {
        Point point = Point.measurement("co2")
            .addTag("sensor", record.getSensorId().toString())
            .addField("co2_value", record.getCo2())
            .time(record.getDate().atZoneSameInstant(ZoneOffset.UTC).toEpochSecond(), WritePrecision.S);

        writeApi.writePoint(point);
    }

    @Override
    public Integer getMaxOverLastNDays(UUID sensorId, int days) {
        var query = Flux.from(bucketName)
            .range(makeNegative(days), ChronoUnit.DAYS)
            .filter(Restrictions.and(
                Restrictions.measurement().equal("co2"),
                Restrictions.tag("sensor").equal(sensorId.toString())
            ))
            .max()
            .toString();

        var result = this.influxDB.getQueryApi().query(query);

        if (result.isEmpty()) {
            return 0;
        } else {
            var record = result.get(0).getRecords().get(0);
            return ((Number) record.getValueByKey("_value")).intValue();
        }
    }

    @Override
    public Double getAverageOverLastNDays(UUID sensorId, int days) {
        var query = Flux.from(bucketName)
            .range(makeNegative(days), ChronoUnit.DAYS)
            .filter(Restrictions.and(
                Restrictions.measurement().equal("co2"),
                Restrictions.tag("sensor").equal(sensorId.toString())
            ))
            .mean()
            .toString();

        var result = this.influxDB.getQueryApi().query(query);

        if (result.isEmpty()) {
            return 0.0;
        } else {
            var record = result.get(0).getRecords().get(0);
            return ((Number) record.getValueByKey("_value")).doubleValue();
        }
    }

    @Override
    public List<Integer> getLastMeasurements(UUID sensorId, int limit, int hours) {
        var query = Flux.from(bucketName)
            .range(makeNegative(hours), ChronoUnit.HOURS)
            .sort(List.of("_time"), true)
            .limit(limit)
            .filter(Restrictions.and(
                Restrictions.measurement().equal("co2"),
                Restrictions.tag("sensor").equal(sensorId.toString())
            ))
            .toString();

        var result = this.influxDB.getQueryApi().query(query);

        if (result.isEmpty()) {
            return List.of();
        } else {
            return result.get(0).getRecords()
                .stream()
                .map(fluxRecord -> ((Number) fluxRecord.getValue()).intValue())
                .collect(Collectors.toList());
        }
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    private static long makeNegative(int value) {
        if (value <= 0) {
            return value;
        } else {
            return -value;
        }
    }
}
