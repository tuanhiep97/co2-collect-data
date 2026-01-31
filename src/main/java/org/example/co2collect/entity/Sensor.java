package org.example.co2collect.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "sensors")
@EntityListeners(AuditingEntityListener.class)
public class Sensor {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private SensorStatus status;

    @Column(name = "consecutive_high_readings", nullable = false)
    private int consecutiveHighReadings;

    @Column(name = "consecutive_low_readings", nullable = false)
    private int consecutiveLowReadings;

    @LastModifiedDate
    @Column(name = "last_updated", nullable = false, updatable = true)
    private OffsetDateTime lastUpdated;

    public Sensor() {
        this.status = SensorStatus.OK;
        this.consecutiveHighReadings = 0;
        this.consecutiveLowReadings = 0;
    }

    public Sensor(UUID id) {
        this();
        this.id = id;
    }

    // Getters and Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public SensorStatus getStatus() {
        return status;
    }

    public void setStatus(SensorStatus status) {
        this.status = status;
    }

    public int getConsecutiveHighReadings() {
        return consecutiveHighReadings;
    }

    public void setConsecutiveHighReadings(int consecutiveHighReadings) {
        this.consecutiveHighReadings = consecutiveHighReadings;
    }

    public int getConsecutiveLowReadings() {
        return consecutiveLowReadings;
    }

    public void setConsecutiveLowReadings(int consecutiveLowReadings) {
        this.consecutiveLowReadings = consecutiveLowReadings;
    }

    public OffsetDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(OffsetDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
