#!/usr/bin/env bash
set -euo pipefail

NAMESPACE="${NAMESPACE:-default}"
FRONTEND_SVC="${FRONTEND_SVC:-frontend-service-app}"

echo "Pods in namespace $NAMESPACE"
kubectl get pods -n "$NAMESPACE"

echo "Services in namespace $NAMESPACE"
kubectl get services -n "$NAMESPACE"

EXTERNAL_IP=$(kubectl get svc "$FRONTEND_SVC" -n "$NAMESPACE" -o jsonpath='{.status.loadBalancer.ingress[0].ip}' 2>/dev/null || true)
if [[ -z "$EXTERNAL_IP" ]]; then
  EXTERNAL_IP=$(kubectl get svc "$FRONTEND_SVC" -n "$NAMESPACE" -o jsonpath='{.status.loadBalancer.ingress[0].hostname}' 2>/dev/null || true)
fi

if [[ -z "$EXTERNAL_IP" ]]; then
  echo "Frontend external IP/hostname not ready yet."
  exit 1
fi

echo "Testing frontend endpoint: http://$EXTERNAL_IP/"
curl -I --max-time 10 "http://$EXTERNAL_IP/"

echo "Kubernetes smoke checks passed."
