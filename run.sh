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
    echo "Database: localhost:5432 (user: co2user, db: co2collect)"
    echo ""
    echo "Starting Spring Boot application..."
    echo "Press Ctrl+C to stop the application."
    echo ""
    ./mvnw spring-boot:run
else
    echo "Error: Database failed to start. Check logs with: $DOCKER_COMPOSE logs"
    exit 1
fi
