package org.fluffytiger.restservice.sensors.repository;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApi;
import com.influxdb.client.WriteOptions;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.dsl.Flux;
import com.influxdb.query.dsl.functions.restriction.Restrictions;
import org.fluffytiger.restservice.sensors.Co2MeasurementsRepository;
import org.fluffytiger.restservice.sensors.MeasureRecord;
import org.fluffytiger.restservice.sensors.payload.LastMeasurements;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class InfluxDbMeasurementsRepository implements Co2MeasurementsRepository {
    private final WriteApi writeApi;
    private final QueryApi queryApi;
    private final String bucketName;

    public InfluxDbMeasurementsRepository(InfluxDBClient influxDB, String bucketName) {
        this.writeApi = influxDB.getWriteApi(WriteOptions
            .builder()
            .batchSize(10000)
            .bufferLimit(50000)
            .flushInterval(1000)
            .build()
        );
        this.queryApi = influxDB.getQueryApi();
        this.bucketName = bucketName;
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

        var result = this.queryApi.query(query);

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

        var result = this.queryApi.query(query);

        if (result.isEmpty()) {
            return 0.0;
        } else {
            var record = result.get(0).getRecords().get(0);
            return ((Number) record.getValueByKey("_value")).doubleValue();
        }
    }

    @Override
    public LastMeasurements getMeasurements(UUID sensorId, Instant start) {
        var query = Flux.from(bucketName)
            .range(start)
            .filter(Restrictions.and(
                Restrictions.measurement().equal("co2"),
                Restrictions.tag("sensor").equal(sensorId.toString())
            ))
            .keep(List.of("_value", "_field", "_time"))
            .toString();

        var tables = this.queryApi.query(query);

        if (tables.isEmpty()) {
            return LastMeasurements.EMPTY;
        } else {
            var records = tables.get(0).getRecords();
            var lastValueAt = records.get(records.size() - 1).getTime();

            return new LastMeasurements(records.stream()
                .map(fluxRecord -> ((Number) fluxRecord.getValue()).intValue())
                .collect(Collectors.toList()),
                lastValueAt
            );
        }
    }

    private static long makeNegative(int value) {
        if (value <= 0) {
            return value;
        } else {
            return -value;
        }
    }
}
