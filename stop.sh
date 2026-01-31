#!/bin/bash

# Stop and remove all containers, networks, and volumes

set -e

echo "Stopping CO2 Sensor Monitoring Service..."

# Determine which docker compose command to use
if command -v docker-compose &> /dev/null; then
    DOCKER_COMPOSE="docker-compose"
else
    DOCKER_COMPOSE="docker compose"
fi

$DOCKER_COMPOSE down -v

echo "Services stopped and cleaned up successfully!"
