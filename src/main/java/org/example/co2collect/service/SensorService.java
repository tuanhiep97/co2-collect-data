package org.example.co2collect.service;

import org.example.co2collect.dto.response.SensorMetricsResponse;
import org.example.co2collect.entity.Measurement;
import org.example.co2collect.entity.Sensor;
import org.example.co2collect.entity.SensorStatus;
import org.example.co2collect.exception.SensorNotFoundException;
import org.example.co2collect.repository.MeasurementRepository;
import org.example.co2collect.repository.SensorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class SensorService {

    private static final int CO2_THRESHOLD = 2000;
    private static final int CONSECUTIVE_THRESHOLD = 3;

    private final SensorRepository sensorRepository;
    private final MeasurementRepository measurementRepository;
    private final AlertService alertService;

    public SensorService(SensorRepository sensorRepository,
            MeasurementRepository measurementRepository,
            AlertService alertService) {
        this.sensorRepository = sensorRepository;
        this.measurementRepository = measurementRepository;
        this.alertService = alertService;
    }

    @Transactional
    public void processMeasurement(UUID sensorUuid, int co2Level, OffsetDateTime timestamp) {
        Measurement measurement = new Measurement(sensorUuid, co2Level, timestamp);
        measurementRepository.save(measurement);

        Sensor sensor = sensorRepository.findById(sensorUuid)
                .orElseGet(() -> new Sensor(sensorUuid));

        updateSensorStatus(sensor, co2Level, timestamp);
        sensorRepository.save(sensor);
    }

    private void updateSensorStatus(Sensor sensor, int co2Level, OffsetDateTime timestamp) {
        SensorStatus previousStatus = sensor.getStatus();

        if (co2Level >= CO2_THRESHOLD) {
            handleHighReading(sensor);
        } else {
            handleLowReading(sensor);
        }

        if (previousStatus == SensorStatus.ALERT && sensor.getStatus() == SensorStatus.OK) {
            alertService.resolveAlert(sensor.getId(), timestamp);
        }
    }

    private void handleHighReading(Sensor sensor) {
        sensor.setConsecutiveHighReadings(sensor.getConsecutiveHighReadings() + 1);
        sensor.setConsecutiveLowReadings(0);

        if (sensor.getStatus() == SensorStatus.OK || sensor.getStatus() == SensorStatus.WARN) {
            sensor.setStatus(SensorStatus.WARN);
        }

        if (sensor.getConsecutiveHighReadings() >= CONSECUTIVE_THRESHOLD) {
            if (sensor.getStatus() != SensorStatus.ALERT) {
                sensor.setStatus(SensorStatus.ALERT);
                alertService.createAlert(sensor.getId());
            }
        }
    }

    private void handleLowReading(Sensor sensor) {
        sensor.setConsecutiveHighReadings(0);

        if (sensor.getStatus() == SensorStatus.ALERT) {
            sensor.setConsecutiveLowReadings(sensor.getConsecutiveLowReadings() + 1);

            if (sensor.getConsecutiveLowReadings() >= CONSECUTIVE_THRESHOLD) {
                sensor.setStatus(SensorStatus.OK);
                sensor.setConsecutiveLowReadings(0);
            }
        } else {
            sensor.setStatus(SensorStatus.OK);
            sensor.setConsecutiveLowReadings(0);
        }
    }

    public SensorStatus getSensorStatus(UUID sensorUuid) {
        Sensor sensor = sensorRepository.findById(sensorUuid)
                .orElseThrow(() -> new SensorNotFoundException("Sensor not found: " + sensorUuid));
        return sensor.getStatus();
    }

    public SensorMetricsResponse getSensorMetrics(UUID sensorUuid) {
        if (!sensorRepository.existsById(sensorUuid)) {
            throw new SensorNotFoundException("Sensor not found: " + sensorUuid);
        }

        OffsetDateTime thirtyDaysAgo = OffsetDateTime.now().minusDays(30);

        Integer avgCo2 = measurementRepository.findAverageCo2BySensorUuidAndTimestampAfter(sensorUuid, thirtyDaysAgo);
        Integer maxCo2 = measurementRepository.findMaxCo2BySensorUuidAndTimestampAfter(sensorUuid, thirtyDaysAgo);

        return new SensorMetricsResponse(
                maxCo2 != null ? maxCo2 : 0,
                avgCo2 != null ? avgCo2 : 0);
    }
}
