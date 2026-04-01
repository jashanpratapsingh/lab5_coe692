#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"

echo "Checking local Tomcat health endpoints on $BASE_URL"
for endpoint in   "$BASE_URL/auth-service/api/health"   "$BASE_URL/catalog-service/api/health"   "$BASE_URL/inventory-service/api/health"   "$BASE_URL/reservation-service/api/health"   "$BASE_URL/checkout-penalty-service/api/health"   "$BASE_URL/frontend-service/"; do
  code=$(curl -s -o /dev/null -w "%{http_code}" "$endpoint" || true)
  echo "$endpoint -> HTTP $code"
  if [[ "$code" -lt 200 || "$code" -ge 500 ]]; then
    echo "Health check failed for $endpoint"
    exit 1
  fi
done

echo "Local smoke checks passed."
