package org.example.co2collect.repository;

import org.example.co2collect.config.JpaConfig;
import org.example.co2collect.entity.Sensor;
import org.example.co2collect.entity.SensorStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaConfig.class)
class SensorRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SensorRepository sensorRepository;

    @BeforeEach
    void setUp() {
        sensorRepository.deleteAll();
    }

    @Test
    void shouldFindSensorById() {
        UUID id = UUID.randomUUID();
        Sensor sensor = new Sensor(id);
        sensor.setStatus(SensorStatus.WARN);
        entityManager.persist(sensor);
        entityManager.flush();

        Optional<Sensor> found = sensorRepository.findById(id);

        assertThat(found).isPresent();
        assertThat(found.get().getStatus()).isEqualTo(SensorStatus.WARN);
    }
}
