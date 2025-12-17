#!/bin/bash
# Auth Backend - Dev Deploy Script
# Server-57 (192.168.0.57)

set -e

# Configuration
DOCKER_REGISTRY="nexus-docker.lotecs.co.kr"
IMAGE_NAME="lotecs-auth-backend"
COMPOSE_DIR="/data/apps"
COMPOSE_FILE="docker-compose.yml"

echo "=========================================="
echo "Auth Backend Deploy - Dev Environment"
echo "=========================================="

cd ${COMPOSE_DIR}

# Pull latest images
echo "[1/5] Pulling latest images..."
docker compose pull auth-backend-1 auth-backend-2

# Rolling update - Instance 1
echo "[2/5] Updating auth-backend-1..."
docker compose stop auth-backend-1
docker compose up -d auth-backend-1

echo "Waiting for auth-backend-1 to be healthy..."
sleep 30

# Health check Instance 1
if curl -sf http://localhost:8100/actuator/health > /dev/null; then
    echo "auth-backend-1 is healthy"
else
    echo "auth-backend-1 health check failed!"
    exit 1
fi

# Rolling update - Instance 2
echo "[3/5] Updating auth-backend-2..."
docker compose stop auth-backend-2
docker compose up -d auth-backend-2

echo "Waiting for auth-backend-2 to be healthy..."
sleep 30

# Health check Instance 2
if curl -sf http://localhost:8101/actuator/health > /dev/null; then
    echo "auth-backend-2 is healthy"
else
    echo "auth-backend-2 health check failed!"
    exit 1
fi

# Cleanup old images
echo "[4/5] Cleaning up old images..."
docker image prune -f

# Show status
echo "[5/5] Deployment Status:"
docker compose ps auth-backend-1 auth-backend-2

echo ""
echo "=========================================="
echo "Deploy completed successfully!"
echo "=========================================="
