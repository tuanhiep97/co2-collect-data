package org.example.co2collect.controller;

import jakarta.validation.Valid;
import org.example.co2collect.dto.request.MeasurementRequest;
import org.example.co2collect.dto.response.SensorMetricsResponse;
import org.example.co2collect.dto.response.SensorStatusResponse;
import org.example.co2collect.service.SensorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sensors")
public class SensorController {

    private final SensorService sensorService;

    public SensorController(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    @PostMapping("/{uuid}/measurements")
    public ResponseEntity<Void> collectMeasurement(
            @PathVariable UUID uuid,
            @Valid @RequestBody MeasurementRequest request) {

        OffsetDateTime timestamp = request.time() != null ? request.time() : OffsetDateTime.now();
        sensorService.processMeasurement(uuid, request.co2(), timestamp);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<SensorStatusResponse> getSensorStatus(@PathVariable UUID uuid) {
        String status = sensorService.getSensorStatus(uuid).name();
        return ResponseEntity.ok(new SensorStatusResponse(status));
    }

    @GetMapping("/{uuid}/metrics")
    public ResponseEntity<SensorMetricsResponse> getSensorMetrics(@PathVariable UUID uuid) {
        SensorMetricsResponse response = sensorService.getSensorMetrics(uuid);
        return ResponseEntity.ok(response);
    }
}
