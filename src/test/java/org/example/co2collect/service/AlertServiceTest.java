package org.example.co2collect.service;

import org.example.co2collect.entity.Alert;
import org.example.co2collect.repository.AlertRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    @Mock
    private AlertRepository alertRepository;

    @InjectMocks
    private AlertService alertService;

    @Test
    void shouldCreateAlert() {
        UUID sensorUuid = UUID.randomUUID();
        when(alertRepository.save(any(Alert.class))).thenAnswer(i -> i.getArgument(0));

        Alert alert = alertService.createAlert(sensorUuid);

        assertThat(alert.getSensorUuid()).isEqualTo(sensorUuid);
        assertThat(alert.isActive()).isTrue();
        verify(alertRepository).save(any(Alert.class));
    }

    @Test
    void shouldResolveAlertIfActive() {
        UUID sensorUuid = UUID.randomUUID();
        Alert activeAlert = new Alert(sensorUuid);
        when(alertRepository.findBySensorUuidAndEndTimeIsNull(sensorUuid)).thenReturn(Optional.of(activeAlert));

        OffsetDateTime endTime = OffsetDateTime.now();
        alertService.resolveAlert(sensorUuid, endTime);

        assertThat(activeAlert.isActive()).isFalse();
        assertThat(activeAlert.getEndTime()).isEqualTo(endTime);
        verify(alertRepository).save(activeAlert);
    }

    @Test
    void shouldDoNothingIfNoActiveAlertToResolve() {
        UUID sensorUuid = UUID.randomUUID();
        when(alertRepository.findBySensorUuidAndEndTimeIsNull(sensorUuid)).thenReturn(Optional.empty());

        alertService.resolveAlert(sensorUuid, OffsetDateTime.now());

        verify(alertRepository, never()).save(any(Alert.class));
    }
}
