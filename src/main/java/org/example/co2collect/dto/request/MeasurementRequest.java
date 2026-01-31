package org.example.co2collect.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.OffsetDateTime;

public record MeasurementRequest(
        @NotNull(message = "CO2 level is required") @Positive(message = "CO2 level must be positive") Integer co2,

        OffsetDateTime time) {
}
