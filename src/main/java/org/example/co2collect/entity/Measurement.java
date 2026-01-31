package org.example.co2collect.entity;

import jakarta.persistence.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "measurements", indexes = {
        @Index(name = "idx_measurements_sensor_timestamp", columnList = "sensor_uuid,timestamp")
})
@EntityListeners(AuditingEntityListener.class)
public class Measurement {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "sensor_uuid", nullable = false, columnDefinition = "UUID")
    private UUID sensorUuid;

    @Column(name = "co2_level", nullable = false)
    private int co2Level;

    @Column(nullable = false)
    private OffsetDateTime timestamp;

    public Measurement() {
    }

    public Measurement(UUID sensorUuid, int co2Level, OffsetDateTime timestamp) {
        this.sensorUuid = sensorUuid;
        this.co2Level = co2Level;
        this.timestamp = timestamp;
    }

    // Getters and Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getSensorUuid() {
        return sensorUuid;
    }

    public void setSensorUuid(UUID sensorUuid) {
        this.sensorUuid = sensorUuid;
    }

    public int getCo2Level() {
        return co2Level;
    }

    public void setCo2Level(int co2Level) {
        this.co2Level = co2Level;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
