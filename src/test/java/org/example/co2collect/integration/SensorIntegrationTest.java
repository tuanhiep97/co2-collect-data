package org.example.co2collect.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.co2collect.dto.request.MeasurementRequest;
import org.example.co2collect.repository.AlertRepository;
import org.example.co2collect.repository.MeasurementRepository;
import org.example.co2collect.repository.SensorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SensorIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SensorRepository sensorRepository;

    @Autowired
    private MeasurementRepository measurementRepository;

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID sensorUuid;

    @BeforeEach
    void setUp() {
        alertRepository.deleteAll();
        measurementRepository.deleteAll();
        sensorRepository.deleteAll();
        sensorUuid = UUID.randomUUID();
    }

    @Test
    void shouldTransitionFromOkToWarn() throws Exception {
        sendMeasurement(2100);

        mockMvc.perform(get("/api/v1/sensors/" + sensorUuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("WARN")));
    }

    @Test
    void shouldTransitionToAlertAfterThreeHighReadings() throws Exception {
        sendMeasurement(2100);
        sendMeasurement(2200);
        sendMeasurement(2300);

        mockMvc.perform(get("/api/v1/sensors/" + sensorUuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("ALERT")));
    }

    @Test
    void shouldTransitionBackToOkAfterThreeLowReadingsFromAlert() throws Exception {
        sendMeasurement(2100);
        sendMeasurement(2200);
        sendMeasurement(2300);

        mockMvc.perform(get("/api/v1/sensors/" + sensorUuid))
                .andExpect(jsonPath("$.status", is("ALERT")));

        sendMeasurement(1000);
        sendMeasurement(1100);
        sendMeasurement(1200);

        mockMvc.perform(get("/api/v1/sensors/" + sensorUuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("OK")));
    }

    @Test
    void shouldResetCountersCorrectly() throws Exception {
        sendMeasurement(2100);
        sendMeasurement(2200);
        sendMeasurement(1000);

        mockMvc.perform(get("/api/v1/sensors/" + sensorUuid))
                .andExpect(jsonPath("$.status", is("OK")));

        sendMeasurement(2100);
        sendMeasurement(2200);
        sendMeasurement(2300);

        mockMvc.perform(get("/api/v1/sensors/" + sensorUuid))
                .andExpect(jsonPath("$.status", is("ALERT")));
    }

    @Test
    void shouldCalculateMetricsCorrectly() throws Exception {
        OffsetDateTime now = OffsetDateTime.now();
        sendMeasurement(1000, now.minusDays(1));
        sendMeasurement(2000, now.minusDays(2));
        sendMeasurement(3000, now.minusDays(31));

        mockMvc.perform(get("/api/v1/sensors/" + sensorUuid + "/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maxLast30Days", is(2000)))
                .andExpect(jsonPath("$.avgLast30Days", is(1500)));
    }

    @Test
    void shouldReturn404WhenSensorNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/sensors/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    private void sendMeasurement(int co2) throws Exception {
        sendMeasurement(co2, OffsetDateTime.now());
    }

    private void sendMeasurement(int co2, OffsetDateTime time) throws Exception {
        MeasurementRequest request = new MeasurementRequest(co2, time);
        mockMvc.perform(post("/api/v1/sensors/" + sensorUuid + "/measurements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}
