package org.example.co2collect.repository;

import org.example.co2collect.config.JpaConfig;
import org.example.co2collect.entity.Alert;
import org.example.co2collect.entity.Sensor;
import org.example.co2collect.entity.SensorStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaConfig.class)
class AlertRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private SensorRepository sensorRepository;

    private UUID sensorUuid;

    @BeforeEach
    void setUp() {
        alertRepository.deleteAll();
        sensorRepository.deleteAll();

        sensorUuid = UUID.randomUUID();
        Sensor sensor = new Sensor(sensorUuid);
        sensor.setStatus(SensorStatus.ALERT);
        entityManager.persist(sensor);
        entityManager.flush();
    }

    @Test
    void shouldFindActiveAlertBySensorUuid() {
        Alert activeAlert = new Alert(sensorUuid);
        entityManager.persist(activeAlert);

        Alert resolvedAlert = new Alert(sensorUuid);
        resolvedAlert.resolve(OffsetDateTime.now());
        entityManager.persist(resolvedAlert);

        entityManager.flush();

        Optional<Alert> found = alertRepository.findBySensorUuidAndEndTimeIsNull(sensorUuid);

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(activeAlert.getId());
    }

    @Test
    void shouldFindAllAlertsBySensorUuid() {
        Alert a1 = new Alert(sensorUuid);
        entityManager.persist(a1);
        Alert a2 = new Alert(sensorUuid);
        entityManager.persist(a2);
        entityManager.flush();

        List<Alert> alerts = alertRepository.findBySensorUuid(sensorUuid);

        assertThat(alerts).hasSize(2);
    }
}
