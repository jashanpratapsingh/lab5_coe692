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

echo "Verifying local Docker images exist"
for svc in "${services[@]}"; do
  docker image inspect "$DOCKER_HUB_USER/$TAG_PREFIX:$svc-app" >/dev/null
  docker image inspect "$DOCKER_HUB_USER/$TAG_PREFIX:$svc-db" >/dev/null
  echo "Found image tags for $svc"
done

echo "Container image smoke checks passed."
