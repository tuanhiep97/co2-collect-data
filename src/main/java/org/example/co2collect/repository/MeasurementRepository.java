package org.example.co2collect.repository;

import org.example.co2collect.entity.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface MeasurementRepository extends JpaRepository<Measurement, UUID> {

        List<Measurement> findBySensorUuidAndTimestampAfter(UUID sensorUuid, OffsetDateTime after);

        @Query("SELECT AVG(m.co2Level) FROM Measurement m WHERE m.sensorUuid = :sensorUuid AND m.timestamp > :after")
        Integer findAverageCo2BySensorUuidAndTimestampAfter(@Param("sensorUuid") UUID sensorUuid,
                        @Param("after") OffsetDateTime after);

        @Query("SELECT MAX(m.co2Level) FROM Measurement m WHERE m.sensorUuid = :sensorUuid AND m.timestamp > :after")
        Integer findMaxCo2BySensorUuidAndTimestampAfter(@Param("sensorUuid") UUID sensorUuid,
                        @Param("after") OffsetDateTime after);
}
