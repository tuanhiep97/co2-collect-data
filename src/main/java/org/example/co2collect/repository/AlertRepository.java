package org.example.co2collect.repository;

import org.example.co2collect.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AlertRepository extends JpaRepository<Alert, UUID> {

    List<Alert> findBySensorUuid(UUID sensorUuid);

    Optional<Alert> findBySensorUuidAndEndTimeIsNull(UUID sensorUuid);
}
