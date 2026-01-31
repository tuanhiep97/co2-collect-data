package org.example.co2collect.service;

import org.example.co2collect.dto.response.SensorMetricsResponse;
import org.example.co2collect.entity.Sensor;
import org.example.co2collect.entity.SensorStatus;
import org.example.co2collect.exception.SensorNotFoundException;
import org.example.co2collect.repository.MeasurementRepository;
import org.example.co2collect.repository.SensorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SensorServiceTest {

    @Mock
    private SensorRepository sensorRepository;
    @Mock
    private MeasurementRepository measurementRepository;
    @Mock
    private AlertService alertService;

    @InjectMocks
    private SensorService sensorService;

    private UUID sensorUuid;

    @BeforeEach
    void setUp() {
        sensorUuid = UUID.randomUUID();
    }

    @Test
    void shouldCreateNewSensorOnFirstMeasurement() {
        when(sensorRepository.findById(sensorUuid)).thenReturn(Optional.empty());

        sensorService.processMeasurement(sensorUuid, 1000, OffsetDateTime.now());

        verify(sensorRepository)
                .save(argThat(sensor -> sensor.getId().equals(sensorUuid) && sensor.getStatus() == SensorStatus.OK));
    }

    @Test
    void shouldTransitionToWarnOnHighReading() {
        Sensor sensor = new Sensor(sensorUuid);
        sensor.setStatus(SensorStatus.OK);
        when(sensorRepository.findById(sensorUuid)).thenReturn(Optional.of(sensor));

        sensorService.processMeasurement(sensorUuid, 2500, OffsetDateTime.now());

        assertThat(sensor.getStatus()).isEqualTo(SensorStatus.WARN);
        assertThat(sensor.getConsecutiveHighReadings()).isEqualTo(1);
    }

    @Test
    void shouldTransitionToAlertAfterThreeHighReadings() {
        Sensor sensor = new Sensor(sensorUuid);
        sensor.setStatus(SensorStatus.WARN);
        sensor.setConsecutiveHighReadings(2);
        when(sensorRepository.findById(sensorUuid)).thenReturn(Optional.of(sensor));

        sensorService.processMeasurement(sensorUuid, 2500, OffsetDateTime.now());

        assertThat(sensor.getStatus()).isEqualTo(SensorStatus.ALERT);
        verify(alertService).createAlert(sensorUuid);
    }

    @Test
    void shouldStayInAlertOnLessThenThreeLowReadings() {
        Sensor sensor = new Sensor(sensorUuid);
        sensor.setStatus(SensorStatus.ALERT);
        sensor.setConsecutiveLowReadings(1);
        when(sensorRepository.findById(sensorUuid)).thenReturn(Optional.of(sensor));

        sensorService.processMeasurement(sensorUuid, 1000, OffsetDateTime.now());

        assertThat(sensor.getStatus()).isEqualTo(SensorStatus.ALERT);
        assertThat(sensor.getConsecutiveLowReadings()).isEqualTo(2);
        verify(alertService, never()).resolveAlert(any(), any());
    }

    @Test
    void shouldTransitionFromAlertToOkAfterThreeLowReadings() {
        Sensor sensor = new Sensor(sensorUuid);
        sensor.setStatus(SensorStatus.ALERT);
        sensor.setConsecutiveLowReadings(2);
        when(sensorRepository.findById(sensorUuid)).thenReturn(Optional.of(sensor));

        sensorService.processMeasurement(sensorUuid, 1000, OffsetDateTime.now());

        assertThat(sensor.getStatus()).isEqualTo(SensorStatus.OK);
        verify(alertService).resolveAlert(eq(sensorUuid), any());
    }

    @Test
    void shouldReturnMetrics() {
        when(sensorRepository.existsById(sensorUuid)).thenReturn(true);
        when(measurementRepository.findAverageCo2BySensorUuidAndTimestampAfter(eq(sensorUuid), any()))
                .thenReturn(1200);
        when(measurementRepository.findMaxCo2BySensorUuidAndTimestampAfter(eq(sensorUuid), any()))
                .thenReturn(2500);

        SensorMetricsResponse metrics = sensorService.getSensorMetrics(sensorUuid);

        assertThat(metrics.avgLast30Days()).isEqualTo(1200);
        assertThat(metrics.maxLast30Days()).isEqualTo(2500);
    }

    @Test
    void shouldThrowExceptionWhenSensorNotFoundForMetrics() {
        when(sensorRepository.existsById(sensorUuid)).thenReturn(false);

        assertThatThrownBy(() -> sensorService.getSensorMetrics(sensorUuid))
                .isInstanceOf(SensorNotFoundException.class);
    }
}
