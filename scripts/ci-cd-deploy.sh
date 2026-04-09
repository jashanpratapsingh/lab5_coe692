#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
NB_DIR="$ROOT_DIR/netbeans-project-structure"
DOCKER_DIR="$ROOT_DIR/docker"
KUBE_DIR="$ROOT_DIR/kube"

DOCKER_HUB_USER="${DOCKER_HUB_USER:?DOCKER_HUB_USER is required}"
DOCKER_HUB_TOKEN="${DOCKER_HUB_TOKEN:?DOCKER_HUB_TOKEN is required}"
TAG_PREFIX="${TAG_PREFIX:-coe692-lab5}"
IMAGE_TAG="${IMAGE_TAG:?IMAGE_TAG is required}"

services=(
  "auth-service"
  "catalog-service"
  "inventory-service"
  "reservation-service"
  "checkout-penalty-service"
  "frontend-service"
)

echo "[1/6] Docker Hub login"
printf '%s' "$DOCKER_HUB_TOKEN" | docker login --username "$DOCKER_HUB_USER" --password-stdin

echo "[2/6] Build WAR artifacts"
for svc in "${services[@]}"; do
  mvn -f "$NB_DIR/$svc/pom.xml" clean package -DskipTests
  cp "$NB_DIR/$svc/target/$svc.war" "$DOCKER_DIR/artifacts/$svc.war"
done

echo "[3/6] Build and push app images tagged with ${IMAGE_TAG}"
for svc in "${services[@]}"; do
  image="$DOCKER_HUB_USER/$TAG_PREFIX:${svc}-app-${IMAGE_TAG}"
  docker build -f "$DOCKER_DIR/Dockerfile-$svc-app" -t "$image" "$DOCKER_DIR"
  docker push "$image"
done

echo "[4/6] Apply base Kubernetes manifests"
kubectl apply -f "$KUBE_DIR/frontend-gateway-config.yaml"
kubectl apply -f "$KUBE_DIR/frontend-backendconfig.yaml"
kubectl apply -f "$KUBE_DIR/lab5service.yaml"
kubectl apply -f "$KUBE_DIR/lab5deployment.yaml"

echo "[5/6] Roll app deployments to newly built image tags"
for svc in "${services[@]}"; do
  image="$DOCKER_HUB_USER/$TAG_PREFIX:${svc}-app-${IMAGE_TAG}"
  kubectl set image "deployment/$svc-app" "$svc-app=$image"
done

echo "[6/6] Wait for rollouts"
for svc in "${services[@]}"; do
  kubectl rollout status "deployment/$svc-app" --timeout=300s
done

echo "Deployment completed with image tag: ${IMAGE_TAG}"
