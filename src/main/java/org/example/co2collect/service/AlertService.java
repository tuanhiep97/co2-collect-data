package org.example.co2collect.service;

import org.example.co2collect.entity.Alert;
import org.example.co2collect.repository.AlertRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class AlertService {

    private final AlertRepository alertRepository;

    public AlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    @Transactional
    public Alert createAlert(UUID sensorUuid) {
        Alert alert = new Alert(sensorUuid);
        return alertRepository.save(alert);
    }

    @Transactional
    public void resolveAlert(UUID sensorUuid, OffsetDateTime endTime) {
        alertRepository.findBySensorUuidAndEndTimeIsNull(sensorUuid)
                .ifPresent(alert -> {
                    alert.resolve(endTime);
                    alertRepository.save(alert);
                });
    }
}
