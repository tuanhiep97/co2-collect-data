package org.example.co2collect.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "alerts", indexes = {
        @Index(name = "idx_alerts_sensor_uuid", columnList = "sensor_uuid")
})
@EntityListeners(AuditingEntityListener.class)
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "sensor_uuid", nullable = false, columnDefinition = "UUID")
    private UUID sensorUuid;

    @CreatedDate
    @Column(name = "start_time", nullable = false, updatable = false)
    private OffsetDateTime startTime;

    @Column(name = "end_time")
    private OffsetDateTime endTime;

    public Alert() {
    }

    public Alert(UUID sensorUuid) {
        this.sensorUuid = sensorUuid;
    }

    public boolean isActive() {
        return endTime == null;
    }

    public void resolve(OffsetDateTime endTime) {
        this.endTime = endTime;
    }

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

    public OffsetDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(OffsetDateTime startTime) {
        this.startTime = startTime;
    }

    public OffsetDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(OffsetDateTime endTime) {
        this.endTime = endTime;
    }
}
