#!/bin/bash

# CO2 Sensor Monitoring Service - Database Setup Script
# This script starts the PostgreSQL database for local development

set -e

echo "Starting PostgreSQL database for CO2 Sensor Monitoring Service..."
echo ""

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "Error: Docker is not running. Please start Docker and try again."
    exit 1
fi

# Determine which docker compose command to use
if command -v docker-compose &> /dev/null; then
    DOCKER_COMPOSE="docker-compose"
else
    DOCKER_COMPOSE="docker compose"
fi

echo "Starting PostgreSQL database..."
$DOCKER_COMPOSE up -d

echo ""
echo "Waiting for database to be ready..."
sleep 5

# Check if database is running
if $DOCKER_COMPOSE ps | grep -q "Up"; then
    echo ""
    echo "Database started successfully!"
    echo ""
    echo "Database Information:"
    echo "   - Host: localhost:5432"
    echo "   - Database: co2collect"
    echo "   - Username: co2user"
    echo "   - Password: co2pass"
    echo ""
    echo "To run the application manually:"
    echo "   ./mvnw spring-boot:run"
    echo ""
    echo "Useful commands:"
    echo "   - View logs: $DOCKER_COMPOSE logs -f"
    echo "   - Stop database: $DOCKER_COMPOSE down"
    echo "   - Connect to DB: docker exec -it co2-collect-db psql -U co2user -d co2collect"
    echo ""
else
    echo "Error: Database failed to start. Check logs with: $DOCKER_COMPOSE logs"
    exit 1
fi
