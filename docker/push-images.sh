#!/usr/bin/env bash
set -euo pipefail

DOCKER_HUB_USER="${DOCKER_HUB_USER:-yourhubusername}"
TAG_PREFIX="${TAG_PREFIX:-coe692-lab5}"

services=(
  "auth-service"
  "catalog-service"
  "inventory-service"
  "reservation-service"
  "checkout-penalty-service"
  "frontend-service"
)

docker login --username="$DOCKER_HUB_USER"

for svc in "${services[@]}"; do
  docker push "$DOCKER_HUB_USER/$TAG_PREFIX:$svc-app"
  docker push "$DOCKER_HUB_USER/$TAG_PREFIX:$svc-db"
done
