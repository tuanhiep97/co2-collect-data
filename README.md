# CO2 Sensor Monitoring Service

A JVM-based service for collecting CO2 sensor measurements and managing alerts.

## Overview

This service handles measurements from hundreds of thousands of sensors, processes CO2 concentration data, manages alert states based on configurable thresholds, and provides metrics through a REST API.

## Features

- **Real-time Monitoring**: Collect CO2 measurements at 1 per minute per sensor
- **Smart Alerting**: Automatic status transitions (OK → WARN → ALERT) based on CO2 levels
- **Metrics Tracking**: 30-day average and maximum CO2 levels per sensor
- **Scalable Architecture**: Designed to handle hundreds of thousands of sensors
- **RESTful API**: Simple and intuitive API endpoints
- **API Documentation**: Interactive Swagger UI for testing

## Technology Stack

- **Language**: Java 21
- **Framework**: Spring Boot 4.0.2
- **Database**: PostgreSQL 16
- **Build Tool**: Maven
- **Containerization**: Docker & Docker Compose
- **API Documentation**: SpringDoc OpenAPI

## Prerequisites

- Docker Desktop (or Docker Engine + Docker Compose)
- Java 21

## Quick Start

The application is running locally while using Docker for dependent resources (PostgreSQL).

1. **Clone the repository**
   ```bash
   git clone https://github.com/tuanhiep97/co2-collect-data.git
   cd co2-collect
   ```

2. **Start the application**
   
   This script will start the PostgreSQL database via Docker and then launch the Spring Boot application.
   ```bash
   chmod +x run.sh
   ./run.sh
   ```

3. **Access the application**
   - Application: http://localhost:8081
   - API Documentation: http://localhost:8081/swagger-ui.html
   - Health Check: http://localhost:8081/actuator/health
   - Database: localhost:5432 (user: co2user, db: co2collect)

4. **Stop the application**
   - Press `Ctrl+C` to stop the Spring Boot app.
   - To stop the database resources:
     ```bash
     docker compose down
     ```

### Running Locally (Without Docker)

1. **Start PostgreSQL**
   ```bash
   # Make sure PostgreSQL is running on localhost:5432
   # Create database: co2collect
   # Create user: co2user with password: co2pass
   ```

2. **Initialize the database**
   ```bash
   psql -U co2user -d co2collect -f docker/init-db/01-schema.sql
   psql -U co2user -d co2collect -f docker/init-db/02-sample-data.sql
   ```

3. **Build and run**
   ```bash
   ./mvnw clean package
   java -jar target/co2-collect-0.0.1-SNAPSHOT.jar
   ```

## API Endpoints

### 1. Collect Sensor Measurements

**POST** `/api/v1/sensors/{uuid}/measurements`

Submit a CO2 measurement for a sensor.

**Request Body:**
```json
{
  "co2": 2000,
  "time": "2019-02-01T18:55:47+00:00"
}
```

**Response:** `201 Created`

### 2. Get Sensor Status

**GET** `/api/v1/sensors/{uuid}`

Retrieve the current status of a sensor.

**Response:**
```json
{
  "status": "OK"
}
```

Possible status values: `OK`, `WARN`, `ALERT`

### 3. Get Sensor Metrics

**GET** `/api/v1/sensors/{uuid}/metrics`

Get 30-day metrics for a sensor.

**Response:**
```json
{
  "maxLast30Days": 1200,
  "avgLast30Days": 900
}
```

## Alert Logic

The service implements a state machine for sensor status:

1. **OK → WARN**: When a single measurement ≥ 2000 ppm
2. **WARN → ALERT**: After 3 consecutive measurements > 2000 ppm
3. **ALERT → OK**: After 3 consecutive measurements < 2000 ppm
4. **Alert Storage**: Each alert records start time and end time

## Database Schema

The application uses three main tables:

- **sensors**: Stores sensor UUID, status, and consecutive reading counters
- **measurements**: Stores all CO2 measurements with timestamps
- **alerts**: Stores alert history with start and end times

Schema is automatically initialized on first startup via `docker/init-db/01-schema.sql`.

## Testing

### Run all tests
```bash
./mvnw test
```

### Run with coverage
```bash
./mvnw test jacoco:report
```

Coverage reports are generated in `target/site/jacoco/index.html`.

## Development

### Project Structure
```
co2-collect/
├── src/
│   ├── main/
│   │   ├── java/org/example/co2collect/
│   │   │   ├── controller/     # REST controllers
│   │   │   ├── service/        # Business logic
│   │   │   ├── repository/     # Data access
│   │   │   ├── entity/         # JPA entities
│   │   │   └── dto/            # Request/Response DTOs
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── docker/
│   └── init-db/                # Database initialization scripts
├── docker-compose.yml
├── Dockerfile
└── pom.xml
```

### Configuration

Key configuration properties in `application.properties`:

- Database connection (overridable via environment variables)
- JPA/Hibernate settings
- Connection pool configuration
- Logging levels
- API documentation paths

## Monitoring

The application exposes actuator endpoints:

- `/actuator/health` - Health check
- `/actuator/info` - Application info
- `/actuator/metrics` - Application metrics
