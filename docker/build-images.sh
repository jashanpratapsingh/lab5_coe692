#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
NB_DIR="$ROOT_DIR/netbeans-project-structure"
DOCKER_DIR="$ROOT_DIR/docker"
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

echo "[1/4] Building WAR files"
for svc in "${services[@]}"; do
  mvn -f "$NB_DIR/$svc/pom.xml" clean package
  cp "$NB_DIR/$svc/target/$svc.war" "$DOCKER_DIR/artifacts/$svc.war"
  cp "$NB_DIR/$svc/db/init.sql" "$DOCKER_DIR/sql/$svc.sql"
done

echo "[2/4] Building app images"
for svc in "${services[@]}"; do
  docker build -f "$DOCKER_DIR/Dockerfile-$svc-app" -t "$DOCKER_HUB_USER/$TAG_PREFIX:$svc-app" "$DOCKER_DIR"
done

echo "[3/4] Building DB images"
for svc in "${services[@]}"; do
  docker build -f "$DOCKER_DIR/Dockerfile-$svc-db" -t "$DOCKER_HUB_USER/$TAG_PREFIX:$svc-db" "$DOCKER_DIR"
done

echo "[4/4] Listing built images"
docker image ls | awk 'NR==1 || /coe692-lab5/'
