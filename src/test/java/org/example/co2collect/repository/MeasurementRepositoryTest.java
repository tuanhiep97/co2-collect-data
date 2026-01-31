package org.example.co2collect.repository;

import org.example.co2collect.config.JpaConfig;
import org.example.co2collect.entity.Measurement;
import org.example.co2collect.entity.Sensor;
import org.example.co2collect.entity.SensorStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaConfig.class)
class MeasurementRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MeasurementRepository measurementRepository;

    @Autowired
    private SensorRepository sensorRepository;

    private UUID sensorUuid;

    @BeforeEach
    void setUp() {
        measurementRepository.deleteAll();
        sensorRepository.deleteAll();

        sensorUuid = UUID.randomUUID();
        Sensor sensor = new Sensor(sensorUuid);
        sensor.setStatus(SensorStatus.OK);
        entityManager.persist(sensor);
        entityManager.flush();
    }

    @Test
    void shouldFindAverageCo2Level() {
        OffsetDateTime now = OffsetDateTime.now();
        Measurement m1 = new Measurement(sensorUuid, 1000, now.minusMinutes(10));
        Measurement m2 = new Measurement(sensorUuid, 2000, now.minusMinutes(5));
        entityManager.persist(m1);
        entityManager.persist(m2);
        entityManager.flush();

        Integer avg = measurementRepository.findAverageCo2BySensorUuidAndTimestampAfter(sensorUuid,
                now.minusMinutes(15));

        assertThat(avg).isEqualTo(1500);
    }

    @Test
    void shouldFindMaxCo2Level() {
        OffsetDateTime now = OffsetDateTime.now();
        Measurement m1 = new Measurement(sensorUuid, 1000, now.minusMinutes(10));
        Measurement m2 = new Measurement(sensorUuid, 2500, now.minusMinutes(5));
        entityManager.persist(m1);
        entityManager.persist(m2);
        entityManager.flush();

        Integer max = measurementRepository.findMaxCo2BySensorUuidAndTimestampAfter(sensorUuid, now.minusMinutes(15));

        assertThat(max).isEqualTo(2500);
    }

    @Test
    void shouldReturnNullWhenNoMeasurementsInRange() {
        OffsetDateTime now = OffsetDateTime.now();
        Measurement m1 = new Measurement(sensorUuid, 1000, now.minusMinutes(20));
        entityManager.persist(m1);
        entityManager.flush();

        Integer avg = measurementRepository.findAverageCo2BySensorUuidAndTimestampAfter(sensorUuid,
                now.minusMinutes(15));
        Integer max = measurementRepository.findMaxCo2BySensorUuidAndTimestampAfter(sensorUuid, now.minusMinutes(15));

        assertThat(avg).isNull();
        assertThat(max).isNull();
    }
}
